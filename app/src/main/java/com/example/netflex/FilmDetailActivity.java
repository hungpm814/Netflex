package com.example.netflex;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.APIRequestModels.PostCommentRequest;
import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.CommentAPIService;
import com.example.netflex.APIServices.FilmAPIService;
import com.example.netflex.adapter.CommentAdapter;
import com.example.netflex.model.Actor;
import com.example.netflex.model.Comment;
import com.example.netflex.model.Film;
import com.example.netflex.model.User;
import com.example.netflex.responseAPI.CommentListResponse;
import com.example.netflex.utils.SharedPreferencesManager;
import com.example.netflex.viewModels.FilmDetailViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private SharedPreferencesManager sharedPreferencesManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        filmId = getIntent().getStringExtra("film_id");

        initApis();
        initServices();
        setupViews();

        Call<FilmDetailViewModel> call = filmAPIService.getFilm(filmId);

        if (filmId != null) {
            loadComments(filmId, page, sort);
            showLoading(true);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<FilmDetailViewModel> call, Response<FilmDetailViewModel> response) {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        Film film = response.body().film;
                        List<Actor> actors = response.body().actors;

                        // Setup viewModel.
                        FilmDetailViewModel viewModel = new FilmDetailViewModel();
                        viewModel.setFilm(film);
                        viewModel.setActors(actors);

                        // Update views' content on the layout.
                        prepareViewsData(viewModel);
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

        textActor.setText("Actors: " + textsForTextActor);
        textYear.setText("Year: " + viewModel.film.getProductionYear());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupViews() {
        poster = findViewById(R.id.imagePoster);
        title = findViewById(R.id.title);
        textAbout = findViewById(R.id.textAbout);
        textActor = findViewById(R.id.textActor);
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
    }

    private void playFilm(Film film) {
        Intent intent = new Intent(FilmDetailActivity.this, WatchFilmActivity.class);
        intent.putExtra("video_url", film.getPath());
        startActivity(intent);
    }

    private void toggleTrailerView(Film film) {
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
}