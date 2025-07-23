package com.example.netflex.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.netflex.APIServices.AuthApiService;
import com.example.netflex.R;
import com.example.netflex.requestAPI.auth.ChangeEmailRequest;
import com.example.netflex.requestAPI.auth.ChangePasswordRequest;
import com.example.netflex.responseAPI.MessageResponse;
import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.utils.SharedPreferencesManager;
import com.example.netflex.utils.MyListManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputEditText etOldPassword, etNewPassword, etConfirmNewPassword;
    private TextInputEditText etNewEmail, etPasswordForEmail;
    private TextView tvCurrentEmail, textMySeriesCount;
    private CardView personalInfoCard, mySeriesCard;
    private Button btnChangePassword, btnChangeEmail, btnLogout;

    private AuthApiService authApiService;
    private SharedPreferencesManager sharedPreferencesManager;
    private MyListManager myListManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        initServices();
        setupClickListeners();
        loadCurrentEmail();
        updateMySeriesCount();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);
        etNewEmail = findViewById(R.id.et_new_email);
        etPasswordForEmail = findViewById(R.id.et_password_for_email);
        tvCurrentEmail = findViewById(R.id.tv_current_email);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnChangeEmail = findViewById(R.id.btn_change_email);
        btnLogout = findViewById(R.id.btn_logout);
        personalInfoCard = findViewById(R.id.card_personal_info);
        mySeriesCard = findViewById(R.id.card_my_series);
        textMySeriesCount = findViewById(R.id.textMySeriesCount);
    }

    private void initServices() {
        authApiService = ApiClient.getRetrofit().create(AuthApiService.class);
        sharedPreferencesManager = new SharedPreferencesManager(this);
        myListManager = new MyListManager(this);
        userId = sharedPreferencesManager.getUserId();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(v -> changePassword());

        btnChangeEmail.setOnClickListener(v -> changeEmail());
        personalInfoCard.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
        
        mySeriesCard.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MySeriesActivity.class);
            startActivity(intent);
        });
        
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

    }

    private void loadCurrentEmail() {
        String currentEmail = sharedPreferencesManager.getUserEmail();
        if (!TextUtils.isEmpty(currentEmail)) {
            tvCurrentEmail.setText(currentEmail);
        }
    }

    private void updateMySeriesCount() {
        int seriesCount = myListManager.getMySeriesCount();
        if (seriesCount > 0) {
            textMySeriesCount.setText("Following " + seriesCount + " series");
        } else {
            textMySeriesCount.setText("View your followed series");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMySeriesCount(); // Update count when returning to this activity
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Thêm method xử lý logout
    private void performLogout() {
        // Xóa tất cả dữ liệu user
        sharedPreferencesManager.logout();

        // Hiển thị toast thông báo
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Chuyển về màn hình login và xóa stack activity
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void changePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmNewPassword.getText().toString().trim();

        if (validatePasswordInputs(oldPassword, newPassword, confirmPassword)) {
            ChangePasswordRequest request = new ChangePasswordRequest(
                    userId, oldPassword, newPassword, confirmPassword
            );

            btnChangePassword.setEnabled(false);
            btnChangePassword.setText("Changing...");

            Call<MessageResponse> call = authApiService.changePassword(request);
            call.enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    btnChangePassword.setEnabled(true);
                    btnChangePassword.setText("Change password");

                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SettingsActivity.this,
                                response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        clearPasswordFields();
                    } else {
                        Toast.makeText(SettingsActivity.this,
                                "Failed to change password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    btnChangePassword.setEnabled(true);
                    btnChangePassword.setText("Change password");
                    Toast.makeText(SettingsActivity.this,
                            "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void changeEmail() {
        String currentEmail = tvCurrentEmail.getText().toString().trim();
        String newEmail = etNewEmail.getText().toString().trim();
        String password = etPasswordForEmail.getText().toString().trim();

        if (validateEmailInputs(newEmail, password)) {
            ChangeEmailRequest request = new ChangeEmailRequest(
                    userId, currentEmail, newEmail, password
            );

            btnChangeEmail.setEnabled(false);
            btnChangeEmail.setText("Changing...");

            Call<MessageResponse> call = authApiService.changeEmail(request);
            call.enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    btnChangeEmail.setEnabled(true);
                    btnChangeEmail.setText("Change email");

                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SettingsActivity.this,
                                response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        // Cập nhật email mới trong SharedPreferences
                        sharedPreferencesManager.saveUserEmail(newEmail);
                        tvCurrentEmail.setText(newEmail);
                        clearEmailFields();
                    } else {
                        Toast.makeText(SettingsActivity.this,
                                "Failed to change email", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    btnChangeEmail.setEnabled(true);
                    btnChangeEmail.setText("Change email");
                    Toast.makeText(SettingsActivity.this,
                            "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validatePasswordInputs(String oldPassword, String newPassword, String confirmPassword) {
        if (TextUtils.isEmpty(oldPassword)) {
            etOldPassword.setError("Old password is required");
            etOldPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return false;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmNewPassword.setError("Passwords do not match");
            etConfirmNewPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateEmailInputs(String newEmail, String password) {
        if (TextUtils.isEmpty(newEmail)) {
            etNewEmail.setError("New email is required");
            etNewEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            etNewEmail.setError("Please enter a valid email address");
            etNewEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPasswordForEmail.setError("Password is required");
            etPasswordForEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void clearPasswordFields() {
        etOldPassword.setText("");
        etNewPassword.setText("");
        etConfirmNewPassword.setText("");
    }

    private void clearEmailFields() {
        etNewEmail.setText("");
        etPasswordForEmail.setText("");
    }
}