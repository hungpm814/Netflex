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
import com.example.netflex.responseAPI.favorite.FavoriteFilmDto;

import java.util.List;

public class FavoriteFilmAdapter extends RecyclerView.Adapter<FavoriteFilmAdapter.ViewHolder> {

    private final Context context;
    private final List<FavoriteFilmDto> filmList;
    private final OnRemoveClickListener listener;

    // Interface duy nhất để xử lý sự kiện xóa
    public interface OnRemoveClickListener {
        void onRemoveClick(FavoriteFilmDto film);
    }

    public FavoriteFilmAdapter(Context context, List<FavoriteFilmDto> filmList, OnRemoveClickListener listener) {
        this.context = context;
        this.filmList = filmList;
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
        FavoriteFilmDto film = filmList.get(position);
        holder.title.setText(film.getTitle());
        holder.type.setText("FILM");

        // Load ảnh poster
        Glide.with(context)
                .load(film.getPoster())
                .placeholder(R.drawable.poster_bg)
                .into(holder.poster);


        // Gán sự kiện xoá
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(film);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filmList.size();
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
