package com.example.netflex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.UserAPIService;
import com.example.netflex.R;
import com.example.netflex.model.User;
import com.example.netflex.utils.SharedPreferencesManager;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private EditText etUserName, etEmail, etPhoneNumber;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;

    private User currentUser;
    private UserAPIService userAPIService;
    private SharedPreferencesManager prefsManager;
    private String currentUserId = "737c2606-b819-4655-a444-d231040d8c38";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        initViews();
        initAPI();
        prefsManager = new SharedPreferencesManager(this);
        currentUserId = prefsManager.getUserId();
        loadUserData();
        setupClickListeners();


    }

    private void initViews() {
        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initAPI() {
        userAPIService = ApiClient.getRetrofit().create(UserAPIService.class);
    }

    private void loadUserData() {
        showLoading(true);

        Call<User> call = userAPIService.getUser(currentUserId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    populateForm();
                } else {
                    Toast.makeText(UserProfileActivity.this, "Unable to load user information", Toast.LENGTH_SHORT).show();
                    loadDemoData();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                Log.e("API_ERROR", "Failed to fetch user", t);
                Toast.makeText(UserProfileActivity.this, "Connection error. Using sample data.", Toast.LENGTH_SHORT).show();
                loadDemoData();
            }
        });
    }

    private void loadDemoData() {
        // Load sample data when API fails
        currentUser = new User();
        currentUser.setId("1");
        currentUser.setUserName("Sample User");
        currentUser.setEmail("user@example.com");
        currentUser.setPhoneNumber("0123456789");
        populateForm();
    }

    private void populateForm() {
        if (currentUser != null) {
            etUserName.setText(currentUser.getUserName());
            etEmail.setText(currentUser.getEmail());
            etPhoneNumber.setText(currentUser.getPhoneNumber());
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveUserProfile());
        btnCancel.setOnClickListener(v -> {
            // Redirect to Home when cancel
            Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void saveUserProfile() {
        if (validateForm()) {
            updateUserFromForm();
            showLoading(true);

            Call<ResponseBody> call = userAPIService.updateUser(currentUserId, currentUser);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    showLoading(false);
                    if (response.isSuccessful()) {
                        Toast.makeText(UserProfileActivity.this, "Update profile successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("API_ERROR", "Update failed with code: " + response.code());
                        Toast.makeText(UserProfileActivity.this, "Unable to update your profile. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showLoading(false);
                    Log.e("API_ERROR", "Failed to update user", t);
                    Toast.makeText(UserProfileActivity.this, "Connection error. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateForm() {
        String userName = etUserName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        // Validate username
        if (TextUtils.isEmpty(userName)) {
            etUserName.setError("Username is required.");
            etUserName.requestFocus();
            return false;
        }

        if (userName.length() > 100) {
            etUserName.setError("Username cannot exceed 100 characters.");
            etUserName.requestFocus();
            return false;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required.");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email address.");
            etEmail.requestFocus();
            return false;
        }

        // Validate phone number (optional)
        if (!TextUtils.isEmpty(phoneNumber) && !Patterns.PHONE.matcher(phoneNumber).matches()) {
            etPhoneNumber.setError("Invalid phone number.");
            etPhoneNumber.requestFocus();
            return false;
        }

        return true;
    }

    private void updateUserFromForm() {
        if (currentUser == null) {
            currentUser = new User();
        }
        currentUser.setId(currentUserId);
        currentUser.setUserName(etUserName.getText().toString().trim());
        currentUser.setEmail(etEmail.getText().toString().trim());
        currentUser.setPhoneNumber(etPhoneNumber.getText().toString().trim());
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }

        // Enable/disable form components during loading
        etUserName.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
        etPhoneNumber.setEnabled(!isLoading);
        btnSave.setEnabled(!isLoading);
        btnCancel.setEnabled(!isLoading);
    }
}
