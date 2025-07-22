package com.example.netflex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.R;
import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.requestAPI.auth.ResetPasswordRequest;
import com.example.netflex.resonseAPI.MessageResponse;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordNew";

    private ImageView btnBack;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnNext;

    private String userEmail;
    private String verifiedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getIntentData();
        initViews();
        setupClickListeners();
    }

    private void getIntentData() {
        userEmail = getIntent().getStringExtra("email");
        verifiedOtp = getIntent().getStringExtra("verified_otp");
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnNext = findViewById(R.id.btn_next);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validatePasswords(newPassword, confirmPassword)) {
                resetPassword(newPassword);
            }
        });
    }

    private boolean validatePasswords(String newPassword, String confirmPassword) {
        // Kiểm tra mật khẩu mới
        if (newPassword.isEmpty()) {
            etNewPassword.setError("Mật khẩu không được để trống");
            etNewPassword.requestFocus();
            return false;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etNewPassword.requestFocus();
            return false;
        }

        // Kiểm tra xác nhận mật khẩu
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return false;
        }

        // Kiểm tra mật khẩu mạnh (tùy chọn)
        if (!isPasswordStrong(newPassword)) {
            etNewPassword.setError("Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường và 1 số");
            etNewPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isPasswordStrong(String password) {
        // Kiểm tra mật khẩu có chứa ít nhất: 1 chữ hoa, 1 chữ thường, 1 số
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUppercase && hasLowercase && hasDigit;
    }

    private void resetPassword(String newPassword) {
        btnNext.setEnabled(false);
        btnNext.setText("Đang cập nhật...");

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail(userEmail);
        request.setNewPassword(newPassword);

        ApiClient.getAuthService().resetPassword(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                btnNext.setEnabled(true);
                btnNext.setText("NEXT →");

                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse messageResponse = response.body();

                    Toast.makeText(ResetPasswordActivity.this,
                            "Đặt lại mật khẩu thành công!", Toast.LENGTH_LONG).show();

                    // Chuyển về màn hình đăng nhập
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    try {
                        String errorMessage = "Không thể đặt lại mật khẩu. Vui lòng thử lại!";
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                        Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ResetPasswordActivity.this,
                                "Đã xảy ra lỗi. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error parsing error response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                btnNext.setEnabled(true);
                btnNext.setText("NEXT →");

                Toast.makeText(ResetPasswordActivity.this,
                        "Lỗi kết nối. Vui lòng kiểm tra internet!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Không cho phép quay lại sau khi đã verify OTP thành công
        // Có thể hiển thị dialog xác nhận
        super.onBackPressed();
    }
}