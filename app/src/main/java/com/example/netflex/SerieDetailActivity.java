package com.example.netflex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.SerieAPIService;
import com.example.netflex.adapter.EpisodeAdapter;
import com.example.netflex.model.Episode;
import com.example.netflex.model.Serie;
import com.example.netflex.resonseAPI.SerieDetailResponse;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SerieDetailActivity extends AppCompatActivity {

    private static final String TAG = "SerieDetailActivity";

    private ImageView poster, btnBack;
    private TextView title, textAbout, textYear, textEpisodes;
    private RecyclerView recyclerEpisodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_serie);

        // Init Views
        poster = findViewById(R.id.imagePoster);
        btnBack = findViewById(R.id.btnBack);
        title = findViewById(R.id.title);
        textAbout = findViewById(R.id.textAbout);
        textYear = findViewById(R.id.textYear);
        textEpisodes = findViewById(R.id.textEpisodes);
        recyclerEpisodes = findViewById(R.id.recyclerEpisodes);

        // Back Button
        btnBack.setOnClickListener(v -> finish());

        // Get serie ID from intent
        String serieId = getIntent().getStringExtra("serie_id");
        if (serieId == null) {
            Toast.makeText(this, "Serie ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call API to get serie detail
        SerieAPIService apiService = ApiClient.getRetrofit().create(SerieAPIService.class);
        apiService.getSerieDetail(serieId).enqueue(new Callback<SerieDetailResponse>() {
            @Override
            public void onResponse(Call<SerieDetailResponse> call, Response<SerieDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Serie serie = response.body().getSerie();
                    List<Episode> episodes = response.body().getEpisodes();

                    if (serie == null) {
                        Toast.makeText(SerieDetailActivity.this, "Serie not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Bind data to UI
                    title.setText(serie.getTitle());
                    textAbout.setText(serie.getAbout());
                    textYear.setText("Năm phát hành: " + serie.getProductionYear());
                    Picasso.get().load(serie.getPoster()).into(poster);

                    // Setup RecyclerView for episodes
                    if (episodes != null && !episodes.isEmpty()) {
                        EpisodeAdapter adapter = new EpisodeAdapter(episodes);
                        recyclerEpisodes.setLayoutManager(new LinearLayoutManager(SerieDetailActivity.this));
                        recyclerEpisodes.setAdapter(adapter);
                    } else {
                        textEpisodes.setText("No episodes found");
                        recyclerEpisodes.setVisibility(RecyclerView.GONE);
                    }

                } else {
                    Toast.makeText(SerieDetailActivity.this, "Failed to load serie detail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerieDetailResponse> call, Throwable t) {
                Toast.makeText(SerieDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API Error", t);
            }
        });
    }
}
