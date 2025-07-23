package com.example.netflex.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.CommentAPIService;
import com.example.netflex.APIServices.ReviewAPIService;
import com.example.netflex.APIServices.SerieAPIService;
import com.example.netflex.R;
import com.example.netflex.adapter.CommentAdapter;
import com.example.netflex.adapter.EpisodeAdapter;
import com.example.netflex.model.Comment;
import com.example.netflex.model.Episode;
import com.example.netflex.model.Genre;
import com.example.netflex.model.Serie;
import com.example.netflex.requestAPI.auth.RatingRequest;
import com.example.netflex.responseAPI.CommentListResponse;
import com.example.netflex.utils.SharedPreferencesManager;
import com.example.netflex.viewModels.ReviewEditModel;
import com.example.netflex.responseAPI.ReviewResponse;
import com.example.netflex.responseAPI.SerieDetailResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SerieDetailActivity extends AppCompatActivity {

    private static final String TAG = "SerieDetailActivity";

    private ImageView poster, btnBack;
    private TextView title, textAbout, textYear, textEpisodes, textNoComments;
    private TextView textGenres, textCountries, textActors;
    private RecyclerView recyclerEpisodes;
    private TextView textRating;
    private RatingBar ratingBar;

    private LinearLayout layoutRating, layoutRateContent;
    private RatingBar ratingBarLike;
    private String serieId;

    private SharedPreferencesManager prefsManager;
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
        prefsManager = new SharedPreferencesManager(this);
        Log.d("DEBUG", "UserId: " + prefsManager.getUserId());
        Log.d("DEBUG", "isLoggedIn: " + prefsManager.isLoggedIn());
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
        textRating = findViewById(R.id.textRating);
        ratingBar = findViewById(R.id.ratingBar);
        layoutRating = findViewById(R.id.layoutRating);
        ratingBarLike = findViewById(R.id.ratingBarLike);
        layoutRateContent = findViewById(R.id.layoutRateContent);
        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        layoutRating.setOnClickListener(v -> {
            boolean isVisible = ratingBarLike.getVisibility() == VISIBLE;
            ratingBarLike.setVisibility(isVisible ? GONE : VISIBLE);
            layoutRateContent.setVisibility(isVisible ? VISIBLE : GONE);
        });
        btnLoadMore = findViewById(R.id.btnLoadMore);
        textNoComments = findViewById(R.id.no_comments);

        nestedScrollView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (ratingBarLike.getVisibility() == VISIBLE) {
                    ratingBarLike.setVisibility(GONE);
                    layoutRateContent.setVisibility(VISIBLE);
                }
            }
            return false;
        });

        btnBack.setOnClickListener(v -> finish());

        serieId = getIntent().getStringExtra("serie_id");
        if (serieId == null) {
            Toast.makeText(this, "Serie ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        initServices();
        initApiServices();
        loadComments(serieId, page, sort);

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

                    title.setText(serie.getTitle());
                    textAbout.setText(serie.getAbout());
                    textYear.setText("Year of release: " + serie.getProductionYear());
                    textGenres.setText("Category: " + genreListToString(response.body().getGenres()));
                    textCountries.setText("Country: " + listToCommaSeparated(response.body().getCountries()));
                    textActors.setText("Actors: " + actorsToString(response.body().getActors()));

                    fetchRating(serie.getId());
                    Picasso.get().load(serie.getPoster()).into(poster);

                    if (episodes != null && !episodes.isEmpty()) {
                        EpisodeAdapter adapter = new EpisodeAdapter(episodes);
                        recyclerEpisodes.setLayoutManager(new LinearLayoutManager(SerieDetailActivity.this));
                        recyclerEpisodes.setAdapter(adapter);
                    } else {
                        textEpisodes.setText("Không tìm thấy tập phim nào");
                        recyclerEpisodes.setVisibility(GONE);
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

        ratingBarLike.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (fromUser) {
                if (!prefsManager.isLoggedIn()) {
                    Intent intent = new Intent(SerieDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    submitSerieRating(serieId, (int) rating);
                }
            }
        });

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

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        return token != null && !token.isEmpty();
    }

    private void submitSerieRating(String serieId, int rating) {
        ReviewEditModel model = new ReviewEditModel();
        model.setRating(rating);
        model.setSerieId(serieId);
        model.setCreaterId(String.valueOf(UUID.fromString(prefsManager.getUserId())));
        ReviewAPIService reviewApi = ApiClient.getRetrofit().create(ReviewAPIService.class);

        reviewApi.submitSerieRating(serieId, model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SerieDetailActivity.this, "Đánh giá thành công!", Toast.LENGTH_SHORT).show();
                    fetchRating(serieId);
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("SubmitRating", "Error: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(SerieDetailActivity.this, "Không thể gửi đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SerieDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void fetchRating(String serieId) {
        Log.d(TAG, "Bắt đầu gọi fetchRating với serieId = " + serieId);
        ReviewAPIService reviewAPI = ApiClient.getRetrofit().create(ReviewAPIService.class);
        reviewAPI.getSeriePublicRating(serieId).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double avg = response.body().getAverageRating();
                    int total = response.body().getTotalReviews();

                    textRating.setText("Rating: " + avg + " (" + total + " reviews)");
                    ratingBar.setRating((float) avg);
                } else {
                    textRating.setText("Rating: N/A");
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                textRating.setText("Rating: N/A");
            }
        });
    }

    private String listToCommaSeparated(List<String> list) {
        if (list == null || list.isEmpty()) return "No information";
        return android.text.TextUtils.join(", ", list);
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
