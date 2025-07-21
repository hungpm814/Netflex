package com.example.netflex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.R;

public class ForgotPasswordOtpActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordOtp";

    private ImageView btnBack;
    private EditText[] otpInputs;
    private Button btnNext;
    private TextView tvEmail;

    private String userEmail;
    private String expectedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_otp);

        getIntentData();
        initViews();
        setupOtpInputs();
        setupClickListeners();
        displayMaskedEmail();
    }

    private void getIntentData() {
        userEmail = getIntent().getStringExtra("email");
        expectedOtp = getIntent().getStringExtra("otp");
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnNext = findViewById(R.id.btn_next);

        // Khởi tạo mảng OTP inputs
        otpInputs = new EditText[6];
        otpInputs[0] = findViewById(R.id.et_otp1);
        otpInputs[1] = findViewById(R.id.et_otp2);
        otpInputs[2] = findViewById(R.id.et_otp3);
        otpInputs[3] = findViewById(R.id.et_otp4);
        otpInputs[4] = findViewById(R.id.et_otp5);
        otpInputs[5] = findViewById(R.id.et_otp6);
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;

            otpInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        // Tự động chuyển sang ô tiếp theo
                        otpInputs[index + 1].requestFocus();
                    }

                    // Kiểm tra nếu đã nhập đủ 6 số
                    if (isOtpComplete()) {
                        btnNext.setBackgroundColor(getResources().getColor(R.color.orange_primary));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // Xử lý phím Backspace
            otpInputs[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (otpInputs[index].getText().toString().isEmpty() && index > 0) {
                        // Chuyển về ô trước đó khi ô hiện tại trống
                        otpInputs[index - 1].requestFocus();
                        otpInputs[index - 1].getText().clear();
                    }
                }
                return false;
            });
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            String enteredOtp = getEnteredOtp();
            if (validateOtp(enteredOtp)) {
                verifyOtp(enteredOtp);
            }
        });
    }

    private void displayMaskedEmail() {
        if (userEmail != null) {
            String maskedEmail = maskEmail(userEmail);
            // Cần cập nhật TextView hiển thị email trong layout
            // tvEmail.setText("Please enter 6 digit code sent to\n" + maskedEmail);
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "****@gmail.com";

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return "**@" + domain;
        }

        return username.substring(0, 2) + "**@" + domain;
    }

    private boolean isOtpComplete() {
        for (EditText input : otpInputs) {
            if (input.getText().toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getEnteredOtp() {
        StringBuilder otp = new StringBuilder();
        for (EditText input : otpInputs) {
            otp.append(input.getText().toString().trim());
        }
        return otp.toString();
    }

    private boolean validateOtp(String otp) {
        if (otp.length() != 6) {
            Toast.makeText(this, "Vui lòng nhập đủ 6 số", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!otp.matches("\\d{6}")) {
            Toast.makeText(this, "OTP chỉ chứa số", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void verifyOtp(String enteredOtp) {
        btnNext.setEnabled(false);
        btnNext.setText("Đang xác thực...");

        // Thực tế sẽ gọi API để verify OTP
        // Ở đây chỉ so sánh với OTP đã nhận
        if (expectedOtp != null && expectedOtp.equals(enteredOtp)) {
            Toast.makeText(this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();

            // Chuyển sang màn hình tạo mật khẩu mới
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            intent.putExtra("email", userEmail);
            intent.putExtra("verified_otp", enteredOtp);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "OTP không chính xác. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            clearOtpInputs();
        }

        btnNext.setEnabled(true);
        btnNext.setText("NEXT →");
    }

    private void clearOtpInputs() {
        for (EditText input : otpInputs) {
            input.getText().clear();
        }
        otpInputs[0].requestFocus();
    }
}