package com.example.netflex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.HomeActivity;
import com.example.netflex.activity.LoginActivity;
import com.example.netflex.R;
import com.example.netflex.activity.SettingsActivity;
import com.example.netflex.adapter.HistoryAdapter;
import com.example.netflex.utils.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class WatchHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private LinearLayout emptyStateLayout;
    private TextView textClearHistory;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferencesManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_history);

        prefsManager = new SharedPreferencesManager(this);

        // Check if user is logged in; redirect to login if not
        if (!prefsManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        initViews();
        setupBottomNavigation();
        loadWatchHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh history data when activity resumes (e.g., after returning from another screen)
        loadWatchHistory();
    }

    /**
     * Initialize views and set up click listeners
     */
    private void initViews() {
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        textClearHistory = findViewById(R.id.textClearHistory);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Set up clear history button
        textClearHistory.setOnClickListener(v -> {
            prefsManager.clearWatchHistory();
            loadWatchHistory(); // Refresh the list immediately
        });

        // Set up RecyclerView with LinearLayoutManager
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Load watch history from SharedPreferences and update UI accordingly
     */
    private void loadWatchHistory() {
        List<String[]> historyData = prefsManager.getWatchHistory();

        if (historyData == null || historyData.isEmpty()) {
            // Show empty state
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerViewHistory.setVisibility(View.GONE);
            textClearHistory.setVisibility(View.GONE);
            return;
        }

        // Set up adapter with history data
        HistoryAdapter adapter = new HistoryAdapter(this, historyData);
        recyclerViewHistory.setAdapter(adapter);

        // Show RecyclerView and clear button
        emptyStateLayout.setVisibility(View.GONE);
        recyclerViewHistory.setVisibility(View.VISIBLE);
        textClearHistory.setVisibility(View.VISIBLE);
    }

    /**
     * Set up bottom navigation with item selected listener
     */
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                startActivity(new Intent(WatchHistoryActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.menu_explore) {
                // TODO: Navigate to explore activity
                return true;
            } else if (itemId == R.id.menu_new) {
                // TODO: Navigate to new & hot activity
                return true;
            } else if (itemId == R.id.menu_History) {
                // Already in this activity
                return true;
            } else if (itemId == R.id.menu_settings) {
                startActivity(new Intent(WatchHistoryActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });

        // Highlight the current menu item
        bottomNavigationView.setSelectedItemId(R.id.menu_History);
    }
}
