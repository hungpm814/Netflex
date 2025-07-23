package com.example.netflex.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.activity.FilmDetailActivity;
import com.example.netflex.R;
import com.example.netflex.model.Film;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    private final List<Film> films;

    public FilmAdapter(List<Film> films) {
        this.films = films;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        Film currentFilm = films.get(position);
        Picasso.get()
                .load(currentFilm.poster)
                .into(holder.imagePoster);
        holder.imagePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, FilmDetailActivity.class);
                intent.putExtra("film_id", currentFilm.id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    public static class FilmViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePoster;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePoster = itemView.findViewById(R.id.imagePoster);
        }
    }
}

