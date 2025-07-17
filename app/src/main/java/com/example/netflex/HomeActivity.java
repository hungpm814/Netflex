package com.example.netflex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.FilmAPIService;
import com.example.netflex.APIServices.SerieAPIService;
import com.example.netflex.adapter.FilmAdapter;
import com.example.netflex.adapter.SerieAdapter;
import com.example.netflex.model.Film;
import com.example.netflex.model.Serie;
import com.example.netflex.responseAPI.FilmResponse;
import com.example.netflex.responseAPI.SerieResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        setupBottomNavigation();

        RecyclerView recyclerOnlyOn = findViewById(R.id.recyclerOnlyOn);
        RecyclerView recyclerPopular = findViewById(R.id.recyclerPopular);
        RecyclerView recyclerTrending = findViewById(R.id.recyclerTrending);
        RecyclerView recyclerReleases = findViewById(R.id.recyclerReleases);

        // Gọi API lấy danh sách Film
        FilmAPIService apiService = ApiClient.getRetrofit().create(FilmAPIService.class);
        Call<FilmResponse> call = apiService.getFilms();

        call.enqueue(new Callback<FilmResponse>() {
            @Override
            public void onResponse(Call<FilmResponse> call, Response<FilmResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Film> films = response.body().items;
                    Log.d("FILM_API", "Films count: " + films.size());

                    setupFilmRecyclerView(recyclerTrending, films);
                    setupFilmRecyclerView(recyclerReleases, films);
                } else {
                    Log.e("FILM_API", "Response failed or body is null");
                }
            }

            @Override
            public void onFailure(Call<FilmResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch films", t);
            }
        });

        // Gọi API lấy danh sách Serie
        SerieAPIService serieAPIService = ApiClient.getRetrofit().create(SerieAPIService.class);
        serieAPIService.getSeries(1).enqueue(new Callback<SerieResponse>() {
            @Override
            public void onResponse(Call<SerieResponse> call, Response<SerieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Serie> series = response.body().items;
                    Log.d("SERIE_API", "Series count: " + series.size());

                    setupSerieRecyclerView(recyclerPopular, series);
                    setupSerieRecyclerView(recyclerOnlyOn, series);
                } else {
                    Log.e("SERIE_API", "Response failed or body is null");
                }
            }

            @Override
            public void onFailure(Call<SerieResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch series", t);
            }
        });
    }

    // Hiển thị danh sách Film
    private void setupFilmRecyclerView(RecyclerView recyclerView, List<Film> films) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new FilmAdapter(films));
    }

    // Hiển thị danh sách Serie
    private void setupSerieRecyclerView(RecyclerView recyclerView, List<Serie> series) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SerieAdapter(this, series));
    }

    // Điều hướng thanh bottom
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                return true;
            } else if (itemId == R.id.menu_new) {
                // TODO: Mở New & Hot
                return true;
            } else if (itemId == R.id.menu_History) {
                // TODO: Mở History
                return true;
            } else if (itemId == R.id.menu_profile) {
                Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.menu_home);
    }
}
