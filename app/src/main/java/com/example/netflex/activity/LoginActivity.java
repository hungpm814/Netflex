package com.example.netflex.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.AuthApiService;
import com.example.netflex.MainActivity;
import com.example.netflex.R;
import com.example.netflex.requestAPI.auth.*;
import com.example.netflex.resonseAPI.auth.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnSignIn;
    private TextView tvError, tvSignUp, tvForgotPassword;
    private CheckBox cbRememberMe;
    private AuthApiService authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo API
        authApi = ApiClient.getAuthService();

        // Ánh xạ view
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvError = findViewById(R.id.tvError);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Sự kiện đăng nhập
        btnSignIn.setOnClickListener(v -> login());

        // Chuyển sang RegisterActivity
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Chuyển sang ForgotPasswordActivity
        tvForgotPassword.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//            startActivity(intent);
        });
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validate
        if (email.isEmpty() || password.isEmpty()) {
            tvError.setText("Email và mật khẩu không được để trống");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        // Ẩn lỗi nếu có
        tvError.setVisibility(View.GONE);

        // Gửi request
        LoginRequest request = new LoginRequest(email, password);
        authApi.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();

                    // Lưu userId nếu "remember me"
                    if (cbRememberMe.isChecked()) {
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        prefs.edit()
                                .putString("userId", res.getUser().getId())
                                .putString("email", res.getUser().getEmail())
                                .putString("userName", res.getUser().getUserName())
                                .apply();
                    }

                    // Chuyển sang màn hình chính
                    Toast.makeText(LoginActivity.this, "Chào " + res.getUser().getUserName(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    tvError.setText("Sai email hoặc mật khẩu");
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                tvError.setText("Không thể kết nối tới máy chủ");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }
}