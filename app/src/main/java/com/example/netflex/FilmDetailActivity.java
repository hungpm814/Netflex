package com.example.netflex;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.FilmAPIService;
import com.example.netflex.model.Actor;
import com.example.netflex.model.Film;
import com.example.netflex.viewModels.FilmDetailViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmDetailActivity extends AppCompatActivity {

    ImageView poster, btnBack;
    TextView title, textAbout, textActor, textYear;
    Button btnTrailer, btnPlay;
    WebView webViewTrailer;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private boolean isTrailerViewOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        String filmId = getIntent().getStringExtra("film_id");
        FilmAPIService apiService = ApiClient.getRetrofit().create(FilmAPIService.class);
        Call<FilmDetailViewModel> call = apiService.getFilm(filmId);

        // Find views by ids.
        setupViews();

        if (filmId != null) {
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<FilmDetailViewModel> call, Response<FilmDetailViewModel> response) {
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

    private void prepareViewsData(FilmDetailViewModel viewModel) {

        // Show play trailer btn if trailerUrl exists.
        if (!viewModel.film.getTrailer().isEmpty()) {
            btnTrailer.setVisibility(View.VISIBLE);
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
            btnPlay.setVisibility(View.VISIBLE);

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

        WebSettings settings = webViewTrailer.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        btnBack.setOnClickListener(v -> finish());

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
                webViewTrailer.setVisibility(View.GONE);

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
                webViewTrailer.setVisibility(View.VISIBLE);

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

            webViewTrailer.setVisibility(View.VISIBLE);
            webViewTrailer.loadData(html, "text/html", "utf-8");
        } else {
            webViewTrailer.setVisibility(View.GONE);
            webViewTrailer.loadUrl("about:blank");
        }
    }

}