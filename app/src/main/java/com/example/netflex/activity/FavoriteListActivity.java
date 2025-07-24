package com.example.netflex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.FavoriteApiService;
import com.example.netflex.R;
import com.example.netflex.adapter.FavoriteFilmAdapter;
import com.example.netflex.adapter.FavoriteSeriesAdapter;
import com.example.netflex.responseAPI.favorite.FavoriteFilmDto;
import com.example.netflex.responseAPI.favorite.FavoriteMessageResponse;
import com.example.netflex.responseAPI.favorite.FavoriteResultDto;
import com.example.netflex.responseAPI.favorite.FavoriteSeriesDto;
import com.example.netflex.utils.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteListActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
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
    protected void onResume() {
        super.onResume();

        userId = sharedPreferencesManager.getUserId();
        if (userId != null) {
            loadFavorites(userId);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        recyclerFavorites = findViewById(R.id.recyclerFavorites);
        int numberOfColumns = 3;


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
        bottomNavigationView = findViewById(R.id.bottomNavigation); // ✅ thêm dòng này
        setupBottomNavigation(); //

        recyclerFavorites.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

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

                    filmList = result.getFavoriteFilms();
                    seriesList = result.getFavoriteSeries();

                    filmAdapter = new FavoriteFilmAdapter(FavoriteListActivity.this, filmList, film -> {
                        removeFavoriteFilm(film);
                    });

                    seriesAdapter = new FavoriteSeriesAdapter(FavoriteListActivity.this, seriesList, serie -> {
                        removeFavoriteSeries(serie);
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

    private void removeFavoriteFilm(FavoriteFilmDto film) {
        FavoriteApiService api = ApiClient.getRetrofit().create(FavoriteApiService.class);
        api.removeFavorite(userId, String.valueOf(film.getFilmId()), null)
                .enqueue(new Callback<FavoriteMessageResponse>() {
                    @Override
                    public void onResponse(Call<FavoriteMessageResponse> call, Response<FavoriteMessageResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(FavoriteListActivity.this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                            filmList.remove(film);
                            filmAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(FavoriteListActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FavoriteMessageResponse> call, Throwable t) {
                        Toast.makeText(FavoriteListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFavoriteSeries(FavoriteSeriesDto series) {
        FavoriteApiService api = ApiClient.getRetrofit().create(FavoriteApiService.class);
        api.removeFavorite(userId, null, String.valueOf(series.getSeriesId()))
                .enqueue(new Callback<FavoriteMessageResponse>() {
                    @Override
                    public void onResponse(Call<FavoriteMessageResponse> call, Response<FavoriteMessageResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(FavoriteListActivity.this, "Đã xóa series khỏi yêu thích", Toast.LENGTH_SHORT).show();
                            seriesList.remove(series);
                            seriesAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(FavoriteListActivity.this, "Xóa series thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FavoriteMessageResponse> call, Throwable t) {
                        Toast.makeText(FavoriteListActivity.this, "Lỗi kết nối khi xóa series", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Điều hướng thanh bottom
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                Intent intent = new Intent(FavoriteListActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_explore) {
                Intent intent = new Intent(FavoriteListActivity.this, FilteredResultActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.menu_favorite) {
                return true;
            } else if (itemId == R.id.menu_History) {
                Intent intent = new Intent(FavoriteListActivity.this, WatchHistoryActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_settings) {
                Intent intent = new Intent(FavoriteListActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.menu_favorite);
    }

}
