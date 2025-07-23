package com.example.netflex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.AuthApiService;
import com.example.netflex.R;
import com.example.netflex.requestAPI.auth.RegisterRequest;
import com.example.netflex.responseAPI.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private ImageView ivPasswordToggle, ivConfirmPasswordToggle;
    private TextView tvSignIn;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Mapping
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        ivPasswordToggle = findViewById(R.id.iv_password_toggle);
        ivConfirmPasswordToggle = findViewById(R.id.iv_confirm_password_toggle);
        tvSignIn = findViewById(R.id.tv_sign_in);

        // Toggle Password visibility
        ivPasswordToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            togglePasswordVisibility(etPassword, isPasswordVisible);
        });

        ivConfirmPasswordToggle.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            togglePasswordVisibility(etConfirmPassword, isConfirmPasswordVisible);
        });

        // Sign Up button
        btnSignUp.setOnClickListener(v -> attemptRegister());

        // Navigate to Login
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void togglePasswordVisibility(EditText editText, boolean visible) {
        if (visible) {
            editText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        editText.setSelection(editText.length());
    }

    private void attemptRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest(email, password, confirmPassword);

        AuthApiService service = ApiClient.getRetrofit().create(AuthApiService.class);
        service.register(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Register successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
