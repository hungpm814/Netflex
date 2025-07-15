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
import com.example.netflex.resonseAPI.FilmResponse;
import com.example.netflex.resonseAPI.SerieResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        FilmAPIService apiService = ApiClient.getRetrofit().create(FilmAPIService.class);
        Call<FilmResponse> call = apiService.getFilms();

        call.enqueue(new Callback<FilmResponse>() {
            @Override
            public void onResponse(Call<FilmResponse> call, Response<FilmResponse> response) {
                if (response.isSuccessful()) {
                    List<Film> films = response.body().items;

//                    List<String> posters = new ArrayList<>();
//                    for (Film film : films) {
//                        posters.add(film.poster);
//                    }

                    setupFilmRecyclerView(recyclerTrending, films);
                    setupFilmRecyclerView(recyclerReleases, films);
                }
            }

            @Override
            public void onFailure(Call<FilmResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch films", t);
            }
        });
        SerieAPIService serieAPIService = ApiClient.getRetrofit().create(SerieAPIService.class);
        serieAPIService.getSeries(1).enqueue(new Callback<SerieResponse>() {
            @Override
            public void onResponse(Call<SerieResponse> call, Response<SerieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Serie> series = response.body().items;
                    Log.d("SERIE_API", "Series count: " + series.size());

                    for (Serie s : series) {
                        Log.d("SERIE_API", "Serie: " + s.getTitle() + " | Poster: " + s.getPoster());
                    }
                    setupSerieRecyclerView(recyclerPopular,series);
                    setupSerieRecyclerView(recyclerOnlyOn, series);  // Or any RecyclerView
                }
            }
            @Override
            public void onFailure(Call<SerieResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch series", t);
            }
        });
    }

    private void setupFilmRecyclerView(RecyclerView recyclerView, List<Film> films) {

    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                // Already on home, do nothing
                return true;
            } else if (itemId == R.id.menu_new) {
                // Handle New & Hot
                return true;
            } else if (itemId == R.id.menu_History) {
                // Handle History
                return true;
            } else if (itemId == R.id.menu_profile) {
                // Open User Profile
                Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
        
        // Set Home as selected by default
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
    }

    private void setupRecyclerView(RecyclerView recyclerView, List<Film> films) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new FilmAdapter(films));
    }

    private void setupSerieRecyclerView(RecyclerView recyclerView, List<Serie> series) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SerieAdapter(this, series));
    }
}