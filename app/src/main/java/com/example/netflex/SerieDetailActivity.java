package com.example.netflex;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.CommentAPIService;
import com.example.netflex.APIServices.SerieAPIService;
import com.example.netflex.adapter.CommentAdapter;
import com.example.netflex.adapter.EpisodeAdapter;
import com.example.netflex.model.Comment;
import com.example.netflex.model.Episode;
import com.example.netflex.model.Genre;
import com.example.netflex.model.Serie;
import com.example.netflex.responseAPI.CommentListResponse;
import com.example.netflex.responseAPI.SerieDetailResponse;
import com.example.netflex.utils.SharedPreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SerieDetailActivity extends AppCompatActivity {

    private static final String TAG = "SerieDetailActivity";

    private ImageView poster, btnBack;
    private TextView title, textAbout, textYear, textEpisodes, textNoComments;
    private TextView textGenres, textCountries, textActors;
    private RecyclerView recyclerEpisodes;
    private CommentAPIService commentAPIService;
    private List<Comment> comments = new ArrayList<>();
    private int pageSize = 5;
    private int page = 1;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private SharedPreferencesManager sharedPreferencesManager;
    private Button btnLoadMore;
    private String sort = "desc";

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
        textGenres = findViewById(R.id.textGenres);
        textCountries = findViewById(R.id.textCountries);
        textActors = findViewById(R.id.textActors);
        recyclerEpisodes = findViewById(R.id.recyclerEpisodes);
        btnLoadMore = findViewById(R.id.btnLoadMore);
        textNoComments = findViewById(R.id.no_comments);

        // Back Button
        btnBack.setOnClickListener(v -> finish());

        // Get serie ID from intent
        String serieId = getIntent().getStringExtra("serie_id");
        if (serieId == null) {
            Toast.makeText(this, "Serie ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        initServices();
        initApiServices();
        loadComments(serieId, page, sort);

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
                    textYear.setText("Year of release: " + serie.getProductionYear());

                    // Format genres, countries, actors
                    textGenres.setText("Category: " + genreListToString(response.body().getGenres()));
                    textCountries.setText("Country: " + listToCommaSeparated(response.body().getCountries()));
                    textActors.setText("Actors: " + actorsToString(response.body().getActors()));


                    Picasso.get().load(serie.getPoster()).into(poster);

                    // Setup RecyclerView for episodes
                    if (episodes != null && !episodes.isEmpty()) {
                        EpisodeAdapter adapter = new EpisodeAdapter(episodes);
                        recyclerEpisodes.setLayoutManager(new LinearLayoutManager(SerieDetailActivity.this));
                        recyclerEpisodes.setAdapter(adapter);
                    } else {
                        textEpisodes.setText("Không tìm thấy tập phim nào");
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

    // Helper method to convert list to comma-separated string
    private String listToCommaSeparated(List<String> list) {
        if (list == null || list.isEmpty()) return "No information";
        return android.text.TextUtils.join(", ", list);
    }

    private void initServices(){
        sharedPreferencesManager = new SharedPreferencesManager(this);
    }

    private void initApiServices(){
        commentAPIService = ApiClient.getRetrofit().create(CommentAPIService.class);
    }

    private void loadComments(String serieId, int page, String sort) {
        Call<CommentListResponse> call = commentAPIService.getComments(serieId, page, pageSize, sort);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<CommentListResponse> call, Response<CommentListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Comment> newComments = response.body().comments;

                    if (newComments.isEmpty()) {
                        textNoComments.setVisibility(VISIBLE);
                        return;
                    }

                    comments.addAll(newComments);
                    setupCommentAdapter(comments);

                    if (response.body().hasMore) {
                        btnLoadMore.setVisibility(VISIBLE);
                    } else {
                        btnLoadMore.setVisibility(GONE);
                    }
                } else {

                    Toast.makeText(SerieDetailActivity.this, "Unable to load comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentListResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch comments", t);
                Toast.makeText(SerieDetailActivity.this, "Failed to fetch comments" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCommentAdapter(List<Comment> comments) {
        recyclerView = findViewById(R.id.recyclerComments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, comments, sharedPreferencesManager.getUserId());
        recyclerView.setAdapter(commentAdapter);
    }
    private String actorsToString(List<com.example.netflex.model.Actor> actors) {
        if (actors == null || actors.isEmpty()) return "No information";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < actors.size(); i++) {
            builder.append(actors.get(i).getName());
            if (i < actors.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
    private String genreListToString(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) return "No information";
        List<String> names = new ArrayList<>();
        for (Genre g : genres) {
            names.add(g.getName());
        }
        return android.text.TextUtils.join(", ", names);
    }
}
