package com.example.netflex.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.R;
import com.example.netflex.SerieDetailActivity;
import com.example.netflex.model.Serie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SerieAdapter extends RecyclerView.Adapter<SerieAdapter.SerieViewHolder> {

    private final List<Serie> series;
    private final Context context;

    public SerieAdapter(Context context, List<Serie> series) {
        this.series = series;
        this.context = context;
    }

    @NonNull
    @Override
    public SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_serie, parent, false);
        return new SerieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SerieViewHolder holder, int position) {
        Serie currentSerie = series.get(position);
        Picasso.get().load(currentSerie.getPoster()).into(holder.imagePoster);

        holder.imagePoster.setOnClickListener(v -> {
            Intent intent = new Intent(context, SerieDetailActivity.class);
            intent.putExtra("serie_id", currentSerie.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    public static class SerieViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePoster;

        public SerieViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePoster = itemView.findViewById(R.id.imagePoster);
        }
    }
}