package com.example.netflex.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflex.R;
import com.example.netflex.responseAPI.favorite.FavoriteSeriesDto;
import android.content.Intent;
import java.util.List;

public class FavoriteSeriesAdapter extends RecyclerView.Adapter<FavoriteSeriesAdapter.ViewHolder> {

    private final Context context;
    private final List<FavoriteSeriesDto> seriesList;
    private final OnRemoveClickListener listener;

    public interface OnRemoveClickListener {
        void onRemoveClick(FavoriteSeriesDto series);
    }

    public FavoriteSeriesAdapter(Context context, List<FavoriteSeriesDto> seriesList, OnRemoveClickListener listener) {
        this.context = context;
        this.seriesList = seriesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteSeriesDto series = seriesList.get(position);
        holder.title.setText(series.getTitle());
        holder.type.setText("SERIES");

        Log.d("FAVORITE_SERIES", "Poster URL: " + series.getPoster());

        Glide.with(context)
                .load(series.getPoster())
                .placeholder(R.drawable.poster_bg)
                .into(holder.poster);
        holder.btnRemove.setOnClickListener(v -> listener.onRemoveClick(series));



        View.OnClickListener goToDetailListener = v -> {
            Log.d("FAVORITE", "Clicked series id: " + series.getSeriesId()); // kiểm tra log
            Intent intent = new Intent(context, com.example.netflex.activity.SerieDetailActivity.class);
            intent.putExtra("serie_id", series.getSeriesId());
            context.startActivity(intent);
        };
        holder.poster.setOnClickListener(goToDetailListener);
        holder.itemView.setOnClickListener(goToDetailListener);

    }

    @Override
    public int getItemCount() {
        return seriesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster, btnRemove;
        TextView title, type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            title = itemView.findViewById(R.id.textTitle);
            type = itemView.findViewById(R.id.textType);
        }
    }
}
