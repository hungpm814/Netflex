package com.example.netflex.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.netflex.R;
import com.example.netflex.utils.MyListManager;
import com.example.netflex.utils.SharedPreferencesManager;

public class WatchFilmActivity extends AppCompatActivity {
    private PlayerView playerView;
    private ExoPlayer player;
    private ImageView btnBack;
    private LinearLayout layoutFollowSeries;
    private ImageView imageFollowSeries;
    private TextView textFollowSeries;
    
    private MyListManager myListManager;
    private SharedPreferencesManager prefsManager;
    
    private String serieId;
    private String serieTitle;
    private String seriePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_film);

        initViews();
        initServices();

        String videoUrl = getIntent().getStringExtra("video_url");
        String episodeTitle = getIntent().getStringExtra("episode_title");
        serieId = getIntent().getStringExtra("serie_id");
        serieTitle = getIntent().getStringExtra("serie_title");
        seriePoster = getIntent().getStringExtra("serie_poster");

        setupPlayer(videoUrl, episodeTitle);
        setupFollowButton();
    }

    private void initViews() {
        playerView = findViewById(R.id.playerView);
        btnBack = findViewById(R.id.btnBack);
        layoutFollowSeries = findViewById(R.id.layoutFollowSeries);
        imageFollowSeries = findViewById(R.id.imageFollowSeries);
        textFollowSeries = findViewById(R.id.textFollowSeries);

        btnBack.setOnClickListener(v -> finish());
    }

    private void initServices() {
        myListManager = new MyListManager(this);
        prefsManager = new SharedPreferencesManager(this);
    }

    private void setupPlayer(String videoUrl, String episodeTitle) {
        if (videoUrl != null) {
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);

            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(Uri.parse(videoUrl))
                    .setMimeType("application/x-mpegURL")
                    .build();

            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();

            if (episodeTitle != null) {
                setTitle(episodeTitle);
            }
        }
    }

    private void setupFollowButton() {
        android.util.Log.d("🔰🔰🔰 WatchFilmActivity", "setupFollowButton called");
        android.util.Log.d("🔰🔰🔰🔰 WatchFilmActivity", "serieId != null: " + (serieId != null));
        android.util.Log.d("🔰🔰🔰🔰🔰 WatchFilmActivity", "serieTitle != null: " + (serieTitle != null));
        
        // Only show follow button if this is a series episode
        if (serieId != null && serieTitle != null) {
            android.util.Log.d("❤️❤️❤️ WatchFilmActivity", "Setting button visible");
            layoutFollowSeries.setVisibility(View.VISIBLE);
            updateFollowButton();

            layoutFollowSeries.setOnClickListener(v -> {
                if (!prefsManager.isLoggedIn()) {
                    Intent intent = new Intent(WatchFilmActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }

                toggleFollowSeries();
            });
        } else {
            android.util.Log.d("⚠️⚠️⚠️ WatchFilmActivity", "Setting button gone");
            layoutFollowSeries.setVisibility(View.GONE);
        }
    }

    private void toggleFollowSeries() {
        if (myListManager.isInMyList(serieId)) {
            // Unfollow series
            myListManager.removeFromMyList(serieId);
            Toast.makeText(this, "Unfollowed " + serieTitle, Toast.LENGTH_SHORT).show();
        } else {
            // Follow series
            myListManager.addToMyList(serieId, serieTitle, seriePoster != null ? seriePoster : "", "serie");
            Toast.makeText(this, "Following " + serieTitle, Toast.LENGTH_SHORT).show();
        }
        updateFollowButton();
    }

    private void updateFollowButton() {
        if (serieId == null) return;

        boolean isFollowing = myListManager.isInMyList(serieId);

        if (isFollowing) {
            textFollowSeries.setText("Following");
            imageFollowSeries.setImageResource(R.drawable.thumb_up_24px);
            imageFollowSeries.setColorFilter(getResources().getColor(android.R.color.holo_green_light));
        } else {
            textFollowSeries.setText("Follow");
            imageFollowSeries.setImageResource(R.drawable.add);
            imageFollowSeries.setColorFilter(getResources().getColor(android.R.color.white));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
