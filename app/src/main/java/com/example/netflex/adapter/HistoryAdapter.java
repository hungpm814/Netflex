package com.example.netflex.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.activity.FilmDetailActivity;
import com.example.netflex.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<String[]> historyList;
    private final Context context;

    public HistoryAdapter(Context context, List<String[]> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        String[] historyData = historyList.get(position);
        String filmId = historyData[0];
        String title = historyData[1];
        String poster = historyData[2];
        long timestamp = Long.parseLong(historyData[3]);

        // Set title
        holder.textTitle.setText(title);

        // Set poster image
        Picasso.get()
                .load(poster)
                .placeholder(R.drawable.poster_bg)
                .error(R.drawable.poster_bg)
                .into(holder.imagePoster);

        // Set watched time
        String timeAgo = getTimeAgo(timestamp);
        holder.textWatchedTime.setText("Đã xem " + timeAgo);

        // Click listener to open FilmDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FilmDetailActivity.class);
            intent.putExtra("film_id", filmId);
            intent.putExtra("film_title", title);
            intent.putExtra("film_poster", poster);
            intent.putExtra("from_history", true);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    /**
     * Calculate time ago from timestamp
     */
    private String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " ngày trước";
        } else if (hours > 0) {
            return hours + " giờ trước";
        } else if (minutes > 0) {
            return minutes + " phút trước";
        } else {
            return "Vừa xong";
        }
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePoster;
        TextView textTitle;
        TextView textWatchedTime;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePoster = itemView.findViewById(R.id.imagePoster);
            textTitle = itemView.findViewById(R.id.textTitle);
            textWatchedTime = itemView.findViewById(R.id.textWatchedTime);
        }
    }
}