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
import com.example.netflex.requestAPI.auth.ForgotPasswordRequest;
import com.example.netflex.responseAPI.auth.ForgotPasswordOtpResponse;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordEmail";

    private ImageView btnBack;
    private TextInputEditText etEmail;
    private Button btnNext;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        etEmail = findViewById(R.id.et_email);
        btnNext = findViewById(R.id.btn_next);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (validateEmail(email)) {
                sendForgotPasswordRequest(email);
            }
        });
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            etEmail.setError("Email không được để trống");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void sendForgotPasswordRequest(String email) {
        // Hiển thị loading state
        btnNext.setEnabled(false);
        btnNext.setText("Đang gửi...");

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(email);

        ApiClient.getAuthService().forgotPassword(request).enqueue(new Callback<ForgotPasswordOtpResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordOtpResponse> call, Response<ForgotPasswordOtpResponse> response) {
                // Reset button state
                btnNext.setEnabled(true);
                btnNext.setText("NEXT →");

                if (response.isSuccessful() && response.body() != null) {
                    ForgotPasswordOtpResponse otpResponse = response.body();
                    userEmail = email;

                    Toast.makeText(ForgotPasswordActivity.this,
                            otpResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    // Chuyển sang màn hình OTP
                    Intent intent = new Intent(ForgotPasswordActivity.this, ForgotPasswordOtpActivity.class);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("otp", otpResponse.getOtp()); // Chỉ để test, thực tế không nên gửi OTP qua Intent
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Không thể gửi email. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordOtpResponse> call, Throwable t) {
                // Reset button state
                btnNext.setEnabled(true);
                btnNext.setText("NEXT →");

                Toast.makeText(ForgotPasswordActivity.this,
                        "Lỗi kết nối. Vui lòng kiểm tra internet!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API call failed", t);
            }
        });
    }
}