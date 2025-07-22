package com.example.netflex.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.R;
import com.example.netflex.model.Comment;
import com.example.netflex.model.User;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        User user = comment.getUser();
        String userName = (user != null && user.getUserName() != null) ? user.getUserName() : "Ẩn danh";
        holder.txtUser.setText(userName);
        holder.txtContent.setText(comment.getContent());
        String createdAt = comment.getCreatedAt();
        holder.txtDate.setText(formatDateTime(createdAt));
    }

    private String formatDateTime(String datetime){
        if (datetime != null && datetime.contains("T")) {
            try {
                String[] dateTimeParts = datetime.split("T");
                String date = dateTimeParts[0];
                String time = dateTimeParts[1].split("\\.")[0];
                return (date + " " + time);
            } catch (Exception e) {
                return "Không rõ";
            }
        } else {
            return "Không rõ";
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtContent, txtDate;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}
