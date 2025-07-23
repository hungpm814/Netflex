package com.example.netflex.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.R;
import com.example.netflex.activity.WatchFilmActivity;
import com.example.netflex.model.Episode;

import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {
    private final List<Episode> episodeList;
    private Context context;
    private String serieId;
    private String serieTitle;
    private String seriePoster;

    public EpisodeAdapter(List<Episode> episodeList) {
        this.episodeList = episodeList;
    }

    public EpisodeAdapter(List<Episode> episodeList, String serieId, String serieTitle, String seriePoster) {
        this.episodeList = episodeList;
        this.serieId = serieId;
        this.serieTitle = serieTitle;
        this.seriePoster = seriePoster;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode episode = episodeList.get(position);
        holder.title.setText(episode.getTitle());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WatchFilmActivity.class);
            intent.putExtra("video_url", episode.getPath());
            intent.putExtra("episode_title", episode.getTitle());
            
            // Add series information if available
            if (serieId != null) {
                intent.putExtra("serie_id", serieId);
                intent.putExtra("serie_title", serieTitle);
                intent.putExtra("serie_poster", seriePoster);
            }
            
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textEpisodeTitle);
        }
    }
}