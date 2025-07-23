package com.example.netflex.activity;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.netflex.APIRequestModels.PostCommentRequest;
import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.CommentAPIService;
import com.example.netflex.APIServices.FilmAPIService;
import com.example.netflex.APIServices.ReviewAPIService;
import com.example.netflex.adapter.CommentAdapter;
import com.example.netflex.R;
import com.example.netflex.model.Actor;
import com.example.netflex.model.Comment;
import com.example.netflex.model.Film;
import com.example.netflex.model.Genre;
import com.example.netflex.model.User;
import com.example.netflex.responseAPI.CommentListResponse;
import com.example.netflex.responseAPI.ReviewResponse;
import com.example.netflex.utils.SharedPreferencesManager;
import com.example.netflex.viewModels.FilmDetailViewModel;
import com.example.netflex.viewModels.ReviewEditModel;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmDetailActivity extends AppCompatActivity {
    private int pageSize = 5;
    ImageView poster, btnBack;
    TextView title, textAbout, textActor, textYear, textNoComments;
    Button btnTrailer, btnPlay, btnLoadMore, btnSendComment;
    WebView webViewTrailer;
    private EditText editTextComment;
    private ProgressBar progressBar;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private boolean isTrailerViewOpened = false;
    private FilmAPIService filmAPIService;
    private CommentAPIService commentAPIService;
    private List<Comment> comments = new ArrayList<>();
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private int page = 1;
    private String sort = "desc";
    private String filmId;
    private TextView textGenres, textCountries, textActors;
    private TextView textRating;
    private RatingBar ratingBar;

    private LinearLayout layoutRating, layoutRateContent;
    private RatingBar ratingBarFilm;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        filmId = getIntent().getStringExtra("film_id");
        if (filmId == null) {
            filmId = getIntent().getStringExtra("id");
        }

        initApis();
        initServices();
        setupViews();

        Call<FilmDetailViewModel> call = filmAPIService.getFilm(filmId);

        if (filmId != null) {
            loadComments(filmId, page, sort);
            fetchRating(filmId);
            showLoading(true);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<FilmDetailViewModel> call, Response<FilmDetailViewModel> response) {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        Film film = response.body().film;
                        List<Actor> actors = response.body().actors;

                        textGenres.setText("Category: " + genreListToString(response.body().getGenres()));
                        textCountries.setText("Country: " + listToCommaSeparated(response.body().getCountries()));
                        textActors.setText("Actors: " + actorsToString(actors));

                        // Setup viewModel.
                        FilmDetailViewModel viewModel = new FilmDetailViewModel();
                        viewModel.setFilm(film);
                        viewModel.setActors(actors);

                        // Update views' content on the layout.
                        prepareViewsData(viewModel);

                        // Save to watch history when viewing film detail
                        SharedPreferencesManager prefsManager = new SharedPreferencesManager(FilmDetailActivity.this);
                        prefsManager.addToWatchHistory(film.getId(), film.getTitle(), film.getPoster(), "film");
                    } else {
                        Toast.makeText(FilmDetailActivity.this, "Failed to load film details", Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<FilmDetailViewModel> call, Throwable t) {
                    Log.e("API_ERROR", "Failed to fetch the film", t);
                }
            });

        } else {
            Toast.makeText(this, "Film ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void initApis() {
        filmAPIService = ApiClient.getRetrofit().create(FilmAPIService.class);
        commentAPIService = ApiClient.getRetrofit().create(CommentAPIService.class);
    }

    private void initServices(){
        sharedPreferencesManager = new SharedPreferencesManager(this);
    }

    private void loadComments(String filmId, int page, String sort) {
        Call<CommentListResponse> call = commentAPIService.getComments(filmId, page, pageSize, sort);
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

                    Toast.makeText(FilmDetailActivity.this, "Unable to load comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentListResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch comments", t);
                Toast.makeText(FilmDetailActivity.this, "Failed to fetch comments" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCommentAdapter(List<Comment> comments) {
        recyclerView = findViewById(R.id.recyclerComments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, comments, sharedPreferencesManager.getUserId());
        recyclerView.setAdapter(commentAdapter);
    }

    private void reloadComments() {
        comments.clear();
        textNoComments.setVisibility(GONE);
        page = 1;
        loadComments(filmId, page, sort);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void postComment() {
        String content = editTextComment.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Comment can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = sharedPreferencesManager.getUserId();

        if (userId == null) {
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        PostCommentRequest request = new PostCommentRequest();
        request.setContent(content);
        request.setUserId(userId);
        request.setFilmId(filmId);

        commentAPIService.postComment(request).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FilmDetailActivity.this, "Comment posted", Toast.LENGTH_SHORT).show();
                    editTextComment.setText("");

                    // Reload comment list.
                    reloadComments();
                } else {
                    Toast.makeText(FilmDetailActivity.this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(FilmDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void prepareViewsData(FilmDetailViewModel viewModel) {

        // Show play trailer btn if trailerUrl exists.
        if (!viewModel.film.getTrailer().isEmpty()) {
            btnTrailer.setVisibility(VISIBLE);
            btnTrailer.setOnClickListener(v -> {
                toggleTrailerView(viewModel.film);
                if (isTrailerViewOpened) {
                    btnTrailer.setText(R.string.hide_trailer);
                } else {
                    btnTrailer.setText(R.string.watch_trailer);
                }
            });
        }

        // Show play btn if path exists.
        if (!viewModel.film.getPath().isEmpty()) {
            btnPlay.setVisibility(VISIBLE);

            btnPlay.setOnClickListener(v -> {
                playFilm(viewModel.film);
            });
        }


        // Set image to poster.
        Picasso.get()
                .load(viewModel.film.poster)
                .into(poster);

        title.setText(viewModel.film.getTitle());
        textAbout.setText(viewModel.film.getAbout());

        String textsForTextActor = "";
        StringBuilder sb = new StringBuilder();

        if (!viewModel.actors.isEmpty()) {
            for (Actor a : viewModel.actors) {
                sb.append(a.getName()).append(", ");
            }
            textsForTextActor = sb.toString();
        } else {
            textsForTextActor = getString(R.string.no_actor_info);
        }

        textYear.setText("Year: " + viewModel.film.getProductionYear());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupViews() {
        poster = findViewById(R.id.imagePoster);
        title = findViewById(R.id.title);
        textAbout = findViewById(R.id.textAbout);
        textGenres = findViewById(R.id.textGenres);
        textCountries = findViewById(R.id.textCountries);
        textActors = findViewById(R.id.textActors);
        textYear = findViewById(R.id.textYear);
        btnTrailer = findViewById(R.id.btnTrailer);
        btnPlay = findViewById(R.id.btnPlay);
        webViewTrailer = findViewById(R.id.webViewTrailer);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        btnLoadMore = findViewById(R.id.btnLoadMore);
        textNoComments = findViewById(R.id.no_comments);
        editTextComment = findViewById(R.id.editTextComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        textRating = findViewById(R.id.textRating);
        ratingBar = findViewById(R.id.ratingBar);
        layoutRateContent = findViewById(R.id.layoutRateContent);
        layoutRating = findViewById(R.id.layoutRating);
        ratingBarFilm = findViewById(R.id.ratingBarFilm);
        SharedPreferences prefs = getSharedPreferences("RATING_PREF", MODE_PRIVATE);
        float savedRating = prefs.getFloat("rating_film_" + filmId, 0f);
        if (savedRating > 0f) {
            ratingBarFilm.setRating(savedRating);
        }

        layoutRating.setOnClickListener(v -> {
            boolean isVisible = ratingBarFilm.getVisibility() == View.VISIBLE;
            ratingBarFilm.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            layoutRateContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        });

        NestedScrollView scrollView = findViewById(R.id.myScrollView);
        if (scrollView != null) {
            scrollView.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (ratingBarFilm.getVisibility() == View.VISIBLE) {
                        ratingBarFilm.setVisibility(View.GONE);
                        layoutRateContent.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            });        } else {
            Log.e("FilmDetailActivity", "NestedScrollView is null");
        }

        if (ratingBarFilm != null) {
            ratingBarFilm.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
                if (fromUser) {
                    if (!sharedPreferencesManager.isLoggedIn()) {
                        startActivity(new Intent(FilmDetailActivity.this, LoginActivity.class));
                    } else {
                        // Lưu ngay vào SharedPreferences khi người dùng chọn
                        SharedPreferences.Editor editor = getSharedPreferences("RATING_PREF", MODE_PRIVATE).edit();
                        editor.putFloat("rating_film_" + filmId, rating);
                        editor.apply();

                        submitfilmRating(filmId, (int) rating);
                    }
                }
            });

        } else {
            Log.e("FilmDetailActivity", "ratingBarLike is null");
        }


        if (webViewTrailer != null) {
            btnBack = findViewById(R.id.btnBack);
            progressBar = findViewById(R.id.progressBar);
            btnLoadMore = findViewById(R.id.btnLoadMore);
            textNoComments = findViewById(R.id.no_comments);
            editTextComment = findViewById(R.id.editTextComment);
            btnSendComment = findViewById(R.id.btnSendComment);

            WebSettings settings = webViewTrailer.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);

            btnBack.setOnClickListener(v -> finish());

            btnSendComment.setOnClickListener(v -> {
                postComment();
            });

            btnLoadMore.setOnClickListener(v -> {
                page++;
                loadComments(getIntent().getStringExtra("film_id"), page, sort);
            });

            webViewTrailer.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onShowCustomView(View view, CustomViewCallback callback) {
                    if (customView != null) {
                        callback.onCustomViewHidden();
                        return;
                    }

                    customView = view;
                    customViewCallback = callback;

                    // Hide trailer webView.
                    webViewTrailer.setVisibility(GONE);

                    // Open full screen mode.
                    FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
                    decorView.addView(customView, new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    ));

                    // Hide system navigation buttons.
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    );
                }

                @Override
                public void onHideCustomView() {
                    if (customView == null) return;

                    // Remove full screen mode.
                    FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
                    decorView.removeView(customView);
                    customView = null;
                    webViewTrailer.setVisibility(VISIBLE);

                    // Show system navigation buttons.
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                    customViewCallback.onCustomViewHidden();
                }
            });
            webViewTrailer.setWebViewClient(new WebViewClient());
        } else {
            Log.e("FilmDetail", "WebView not found in layout");
        }

    }

    private void playFilm(Film film) {
        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        prefsManager.addToWatchHistory(film.getId(), film.getTitle(), film.getPoster(), "film");
        Intent intent = new Intent(FilmDetailActivity.this, WatchFilmActivity.class);
        intent.putExtra("video_url", film.getPath());
        startActivity(intent);
    }

    private void toggleTrailerView(Film film) {
        if (webViewTrailer == null) return;
        isTrailerViewOpened = !isTrailerViewOpened;
        Log.d("trailer", "isTrailerViewOpened: " + isTrailerViewOpened);

        if (isTrailerViewOpened) {
            String youtubeUrl = film.getTrailer();
            String html = "<iframe width=\"100%\" height=\"100%\" src=\"" +
                    youtubeUrl + "\" frameborder=\"0\" allowfullscreen></iframe>";

            webViewTrailer.setVisibility(VISIBLE);
            webViewTrailer.loadData(html, "text/html", "utf-8");
        } else {
            webViewTrailer.setVisibility(GONE);
            webViewTrailer.loadUrl("about:blank");
        }
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? VISIBLE : GONE);
        }
    }

    private void fetchRating(String filmId) {
        Log.d(TAG, "Bắt đầu gọi fetchRating với serieId = " + filmId);
        ReviewAPIService reviewAPI = ApiClient.getRetrofit().create(ReviewAPIService.class);
        reviewAPI.getFilmPublicRating(filmId).enqueue(new Callback<ReviewResponse>() {
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
    private void submitfilmRating(String filmId, int rating) {
        ReviewEditModel model = new ReviewEditModel();
        model.setRating(rating);
        model.setFilmId(filmId);
        model.setCreaterId(String.valueOf(UUID.fromString(sharedPreferencesManager.getUserId())));
        ReviewAPIService reviewApi = ApiClient.getRetrofit().create(ReviewAPIService.class);

        reviewApi.submitFilmRating(filmId, model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FilmDetailActivity.this, "Đánh giá thành công!", Toast.LENGTH_SHORT).show();
                    fetchRating(filmId);
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("SubmitRating", "Error: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(FilmDetailActivity.this, "Không thể gửi đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(FilmDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String genreListToString(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) return "No information";
        List<String> names = new ArrayList<>();
        for (Genre g : genres) {
            names.add(g.getName());
        }
        return android.text.TextUtils.join(", ", names);
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
}