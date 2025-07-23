package com.example.netflex.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.R;
import com.example.netflex.adapter.MyListAdapter;
import com.example.netflex.utils.MyListManager;
import com.example.netflex.utils.SharedPreferencesManager;

import java.util.List;

public class MySeriesActivity extends AppCompatActivity {

    private RecyclerView recyclerMySeries;
    private LinearLayout emptyStateLayout;
    private TextView emptyStateText;
    private ImageView btnBack;
    private MyListManager myListManager;
    private SharedPreferencesManager prefsManager;
    private MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_series);

        initViews();
        initServices();
        setupRecyclerView();
        loadMySeries();
    }

    private void initViews() {
        recyclerMySeries = findViewById(R.id.recyclerMySeries);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        emptyStateText = findViewById(R.id.emptyStateText);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    private void initServices() {
        myListManager = new MyListManager(this);
        prefsManager = new SharedPreferencesManager(this);
    }

    private void setupRecyclerView() {
        recyclerMySeries.setLayoutManager(new GridLayoutManager(this, 3));
    }

    private void loadMySeries() {
        List<MyListManager.MyListItem> mySeries = myListManager.getMySeries();
        
        if (mySeries.isEmpty()) {
            showEmptyState();
        } else {
            showMySeries(mySeries);
        }
    }

    private void showEmptyState() {
        recyclerMySeries.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
        emptyStateText.setText("You haven't followed any series yet.\nStart following your favorite series!");
    }

    private void showMySeries(List<MyListManager.MyListItem> mySeries) {
        recyclerMySeries.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
        
        adapter = new MyListAdapter(mySeries, this::onItemRemoved);
        recyclerMySeries.setAdapter(adapter);
    }

    private void onItemRemoved(String itemId) {
        myListManager.removeFromMyList(itemId);
        loadMySeries(); // Refresh the list
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMySeries(); // Refresh when coming back to this activity
    }
}
