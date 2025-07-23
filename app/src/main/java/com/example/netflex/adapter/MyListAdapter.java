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

import com.example.netflex.R;
import com.example.netflex.activity.FilmDetailActivity;
import com.example.netflex.activity.SerieDetailActivity;
import com.example.netflex.utils.MyListManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyListViewHolder> {

    private List<MyListManager.MyListItem> myList;
    private OnItemRemovedListener listener;

    public interface OnItemRemovedListener {
        void onItemRemoved(String itemId);
    }

    public MyListAdapter(List<MyListManager.MyListItem> myList, OnItemRemovedListener listener) {
        this.myList = myList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_list, parent, false);
        return new MyListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyListViewHolder holder, int position) {
        MyListManager.MyListItem item = myList.get(position);
        
        holder.title.setText(item.title);
        holder.type.setText(item.type.toUpperCase());
        
        // Load poster image
        if (item.poster != null && !item.poster.isEmpty()) {
            Picasso.get()
                    .load(item.poster)
                    .placeholder(R.drawable.poster_bg)
                    .error(R.drawable.poster_bg)
                    .into(holder.poster);
        } else {
            holder.poster.setImageResource(R.drawable.poster_bg);
        }

        // Click to view details
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent;
            
            if ("film".equals(item.type)) {
                intent = new Intent(context, FilmDetailActivity.class);
                intent.putExtra("film_id", item.id);
            } else {
                intent = new Intent(context, SerieDetailActivity.class);
                intent.putExtra("serie_id", item.id);
            }
            
            context.startActivity(intent);
        });

        // Remove button
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemRemoved(item.id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList != null ? myList.size() : 0;
    }

    static class MyListViewHolder extends RecyclerView.ViewHolder {
        ImageView poster, btnRemove;
        TextView title, type;

        public MyListViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            title = itemView.findViewById(R.id.textTitle);
            type = itemView.findViewById(R.id.textType);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
