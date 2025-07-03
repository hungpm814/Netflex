package com.example.netflex;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.FilmAPIService;
import com.example.netflex.adapter.FilmAdapter;
import com.example.netflex.model.Film;
import com.example.netflex.resonseAPI.FilmResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

                    setupRecyclerView(recyclerOnlyOn, films);
                    setupRecyclerView(recyclerPopular, films);
                    setupRecyclerView(recyclerTrending, films);
                    setupRecyclerView(recyclerReleases, films);
                }
            }

            @Override
            public void onFailure(Call<FilmResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch films", t);
            }
        });

    }

    private void setupRecyclerView(RecyclerView recyclerView, List<Film> films) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new FilmAdapter(films));
    }
}