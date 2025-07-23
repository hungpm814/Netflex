package com.example.netflex.activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.netflex.R;

public class WatchFilmActivity extends AppCompatActivity {
    private PlayerView playerView;
    private ExoPlayer player;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_film);

        playerView = findViewById(R.id.playerView);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        String videoUrl = getIntent().getStringExtra("video_url");
        String episodeTitle = getIntent().getStringExtra("episode_title");

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
                setTitle(episodeTitle); // Set title in toolbar
            }
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
