package com.example.netflex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "NetflexPrefs";

    // Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // Remember Me keys
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_SAVED_EMAIL = "saved_email";
    private static final String KEY_SAVED_PASSWORD = "saved_password";

    private static final String KEY_WATCH_HISTORY = "watch_history";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // User session
    public void saveUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    public void saveUserEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Remember Me
    public void saveRememberMeData(String email, String password, boolean rememberMe) {
        if (rememberMe) {
            editor.putBoolean(KEY_REMEMBER_ME, true);
            editor.putString(KEY_SAVED_EMAIL, email);
            editor.putString(KEY_SAVED_PASSWORD, password);
        } else {
            editor.remove(KEY_REMEMBER_ME);
            editor.remove(KEY_SAVED_EMAIL);
            editor.remove(KEY_SAVED_PASSWORD);
        }
        editor.apply();
    }

    public boolean isRememberMeChecked() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
    }

    public String getSavedEmail() {
        return sharedPreferences.getString(KEY_SAVED_EMAIL, "");
    }

    public String getSavedPassword() {
        return sharedPreferences.getString(KEY_SAVED_PASSWORD, "");
    }

    public void logout() {
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.apply();
    }

    // Xoá tất cả dữ liệu
    public void clearUserData() {
        editor.clear();
        editor.apply();
    }


    public void addToWatchHistory(String id, String title, String poster, String type) {  // Thêm param type
        String currentHistory = sharedPreferences.getString(KEY_WATCH_HISTORY, "");
        String newEntry = id + "|" + title + "|" + poster + "|" + System.currentTimeMillis() + "|" + type;  // Thêm type

        if (currentHistory.isEmpty()) {
            currentHistory = newEntry;
        } else {
            // Remove if already exists (to avoid duplicates and move to top)
            String[] entries = currentHistory.split(";;;");
            StringBuilder updatedHistory = new StringBuilder(newEntry);

            int count = 0;
            for (String entry : entries) {
                if (!entry.startsWith(id + "|") && count < 19) { // Keep max 20 items
                    updatedHistory.append(";;;").append(entry);
                    count++;
                }
            }
            currentHistory = updatedHistory.toString();
        }

        editor.putString(KEY_WATCH_HISTORY, currentHistory);
        editor.apply();
    }

    public List<String[]> getWatchHistory() {
        String history = sharedPreferences.getString(KEY_WATCH_HISTORY, "");
        List<String[]> historyList = new ArrayList<>();

        if (!history.isEmpty()) {
            String[] entries = history.split(";;;");
            for (String entry : entries) {
                String[] parts = entry.split("\\|");
                if (parts.length >= 5) {
                    historyList.add(parts);
                }
            }
        }

        return historyList;
    }

    public void clearWatchHistory() {
        editor.remove(KEY_WATCH_HISTORY);
        editor.apply();
    }
}
