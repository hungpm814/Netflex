package com.example.netflex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.AuthApiService;
import com.example.netflex.R;
import com.example.netflex.requestAPI.auth.*;
import com.example.netflex.responseAPI.auth.*;
import com.example.netflex.utils.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnSignIn, btnGoogle;
    private TextView tvError, tvSignUp, tvForgotPassword;
    private CheckBox cbRememberMe;
    private AuthApiService authApi;
    private SharedPreferencesManager prefsManager;


    // Constants for SharedPreferences
    private static final String PREFS_NAME = "login_prefs";
    private static final String KEY_EMAIL = "saved_email";
    private static final String KEY_PASSWORD = "saved_password";
    private static final String KEY_REMEMBER = "remember_me";
    private static final String APP_PREFS = "app_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefsManager = new SharedPreferencesManager(this);

        // Khởi tạo API
        authApi = ApiClient.getAuthService();

        // Initialize views
        initViews();

        // Load saved login data
        loadSavedLoginData();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnGoogle = findViewById(R.id.btnGoogle); // Thêm nếu có trong layout
        tvError = findViewById(R.id.tvError);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void loadSavedLoginData() {
        if (prefsManager.isRememberMeChecked()) {
            etEmail.setText(prefsManager.getSavedEmail());
            etPassword.setText(prefsManager.getSavedPassword());
            cbRememberMe.setChecked(true);
        }
    }
    private void saveLoginData(String email, String password, boolean remember) {
        prefsManager.saveRememberMeData(email, password, remember);
    }


    private void setClickListeners() {
        // Sự kiện đăng nhập
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignIn();
            }
        });

        // Google login (nếu có)
        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleGoogleLogin();
                }
            });
        }

        // Chuyển sang RegisterActivity
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        // Chuyển sang ForgotPasswordActivity
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });
    }

    private void handleSignIn() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (email.isEmpty()) {
            etEmail.setError("Email là bắt buộc");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Mật khẩu là bắt buộc");
            etPassword.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            etEmail.setError("Vui lòng nhập email hợp lệ");
            etEmail.requestFocus();
            return;
        }

        // Ẩn lỗi nếu có
        tvError.setVisibility(View.GONE);
        hideInputErrors();

        // Gọi API login
        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);

        authApi.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Lưu thông tin remember me
                    saveLoginData(email, password, cbRememberMe.isChecked());

                    // Lưu thông tin user session
                    saveUserSession(loginResponse);

                    tvError.setVisibility(View.GONE);

                    // Hiển thị thông báo thành công
                    Toast.makeText(LoginActivity.this,
                            "Chào mừng " + loginResponse.getUser().getUserName(),
                            Toast.LENGTH_SHORT).show();

                    // Kiểm tra role và điều hướng
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("userId", loginResponse.getUser().getId());
                    startActivity(intent);
                } else {
                    // Xử lý lỗi từ server
                    String errorMessage = "Sai email hoặc mật khẩu";
                    if (response.code() == 401) {
                        errorMessage = "Email hoặc mật khẩu không đúng";
                    } else if (response.code() == 500) {
                        errorMessage = "Lỗi server, vui lòng thử lại sau";
                    }

                    showError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showError("Không thể kết nối tới máy chủ. Vui lòng kiểm tra kết nối internet.");
            }
        });
    }

    private void saveUserSession(LoginResponse loginResponse) {
        prefsManager.saveUserId(loginResponse.getUser().getId());
        prefsManager.saveUserEmail(loginResponse.getUser().getEmail());
        prefsManager.setLoggedIn(true);
    }


//    private void navigateBasedOnRole(User user) {
//        Intent intent;
//
//        // Kiểm tra role và điều hướng (giả sử có field role)
//        if (user.getRole() != null && user.getRole().equals("ADMIN")) {
//            // Điều hướng tới Admin Dashboard
//            intent = new Intent(LoginActivity.this, MainActivity.class);
//            intent.putExtra("userId", user.getId());
//            intent.putExtra("isAdmin", true);
//        } else {
//            // Điều hướng tới Home Activity cho user thường
//            intent = new Intent(LoginActivity.this, HomeActivity.class);
//            intent.putExtra("userId", user.getId());
//            intent.putExtra("isAdmin", false);
//        }
//
//        startActivity(intent);
//        finish();
//    }

    private void handleGoogleLogin() {
        Toast.makeText(this, "Đăng nhập với Google", Toast.LENGTH_SHORT).show();
        // TODO: Implement Google Sign-In logic here
    }

    private void handleForgotPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void handleSignUp() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideInputErrors() {
        etEmail.setError(null);
        etPassword.setError(null);
    }
}