package com.example.netflex.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.FavoriteApiService;
import com.example.netflex.R;
import com.example.netflex.adapter.FavoriteFilmAdapter;
import com.example.netflex.adapter.FavoriteSeriesAdapter;
import com.example.netflex.responseAPI.favorite.FavoriteFilmDto;
import com.example.netflex.responseAPI.favorite.FavoriteResultDto;
import com.example.netflex.responseAPI.favorite.FavoriteSeriesDto;
import com.example.netflex.utils.SharedPreferencesManager;
import com.google.gson.Gson;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteListActivity extends AppCompatActivity {

    private RecyclerView recyclerFavorites;
    private TextView txtNoResult;
    private Button btnFilms, btnSeries;

    private FavoriteFilmAdapter filmAdapter;
    private FavoriteSeriesAdapter seriesAdapter;
    private String userId;

    private List<FavoriteFilmDto> filmList;
    private List<FavoriteSeriesDto> seriesList;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        userId = sharedPreferencesManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        recyclerFavorites = findViewById(R.id.recyclerFavorites);
        txtNoResult = findViewById(R.id.txtNoResult);
        btnFilms = findViewById(R.id.btnFavoriteFilms);
        btnSeries = findViewById(R.id.btnFavoriteSeries);

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));

        btnFilms.setOnClickListener(v -> showFilms());
        btnSeries.setOnClickListener(v -> showSeries());

        loadFavorites(userId);
    }

    private void loadFavorites(String userId) {
        FavoriteApiService api = ApiClient.getRetrofit().create(FavoriteApiService.class);
        api.getFavoritesByUser(userId).enqueue(new Callback<FavoriteResultDto>() {
            @Override
            public void onResponse(Call<FavoriteResultDto> call, Response<FavoriteResultDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FavoriteResultDto result = response.body();

                    // ✅ Log toàn bộ JSON để kiểm tra response
                    Log.d("API_RESPONSE", new Gson().toJson(result));

                    // ✅ Kiểm tra từng film poster
                    if (result.getFavoriteFilms() != null) {
                        for (FavoriteFilmDto film : result.getFavoriteFilms()) {
                            Log.d("FAVORITE_POSTER", "Poster URL: " + film.getPoster());
                        }
                    }

                    filmList = result.getFavoriteFilms();
                    seriesList = result.getFavoriteSeries();

                    // Khởi tạo adapter
                    filmAdapter = new FavoriteFilmAdapter(FavoriteListActivity.this, filmList, film -> {
                        // TODO: xử lý xóa film nếu cần
                    });

                    seriesAdapter = new FavoriteSeriesAdapter(FavoriteListActivity.this, seriesList, serie -> {
                        // TODO: xử lý xóa series nếu cần
                    });

                    showFilms();
                } else {
                    Toast.makeText(FavoriteListActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                    showEmpty();
                }
            }


            @Override
            public void onFailure(Call<FavoriteResultDto> call, Throwable t) {
                Toast.makeText(FavoriteListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage(), t);
                showEmpty();
            }
        });
    }

    private void showFilms() {
        if (filmList != null && !filmList.isEmpty()) {
            recyclerFavorites.setAdapter(filmAdapter);
            txtNoResult.setVisibility(View.GONE);
            recyclerFavorites.setVisibility(View.VISIBLE);
        } else {
            showEmpty();
        }
    }

    private void showSeries() {
        if (seriesList != null && !seriesList.isEmpty()) {
            recyclerFavorites.setAdapter(seriesAdapter);
            txtNoResult.setVisibility(View.GONE);
            recyclerFavorites.setVisibility(View.VISIBLE);
        } else {
            showEmpty();
        }
    }

    private void showEmpty() {
        recyclerFavorites.setVisibility(View.GONE);
        txtNoResult.setVisibility(View.VISIBLE);
    }


}
