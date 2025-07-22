package com.example.netflex.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.APIRequestModels.EditCommentRequest;
import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.CommentAPIService;
import com.example.netflex.R;
import com.example.netflex.model.Comment;
import com.example.netflex.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private List<Comment> commentList;
    private String currentUserId;
    private CommentAPIService commentAPIService;
    public CommentAdapter(Context context, List<Comment> comments, String currentUserId) {
        this.context = context;
        this.commentList = comments;
        this.currentUserId = currentUserId;
        commentAPIService = ApiClient.getRetrofit().create(CommentAPIService.class);
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

        // Chỉ hiển thị menu nếu là comment của chính user
        if (comment.getUser().getId().equals(currentUserId)) {
            holder.btnCommentMenu.setVisibility(View.VISIBLE);
        } else {
            holder.btnCommentMenu.setVisibility(View.GONE);
        }

        holder.btnCommentMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.btnCommentMenu);
            popup.inflate(R.menu.comment_menu);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_edit) {
                    showEditDialog(comment);
                } else if (item.getItemId() == R.id.menu_delete) {
                    showDeleteConfirmDialog(comment);
                    return true;
                } else {
                    return false;
                }
                return false;
            });
            popup.show();
        });
    }

    private void showDeleteConfirmDialog(Comment comment) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to delete the comment?")
                .setPositiveButton("Delete", (dialog, which) -> deleteComment(comment.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Comment");

        final EditText input = new EditText(context);
        input.setText(comment.getContent());
        input.setSelection(comment.getContent().length());

        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedContent = input.getText().toString().trim();
            if (!updatedContent.isEmpty()) {
                updateComment(comment.getId(), updatedContent);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateComment(String commentId, String newContent) {
        EditCommentRequest request = new EditCommentRequest();
        request.setCommentId(commentId);
        request.setContent(newContent);

        commentAPIService.editComment(request, request.getCommentId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Comment updated", Toast.LENGTH_SHORT).show();
                    for (Comment c : commentList) {
                        if (c.getId().equals(commentId)) {
                            c.setContent(newContent);
                            break;
                        }
                    }
                    notifyDataSetChanged();

                } else {
                    Toast.makeText(context, "Failed to update comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteComment(String commentId) {
        commentAPIService.deleteComment(commentId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                    // Remove from the list and update.
                    for (int i = 0; i < commentList.size(); i++) {
                        if (commentList.get(i).getId().equals(commentId)) {
                            commentList.remove(i);
                            notifyItemRemoved(i);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String formatDateTime(String datetime) {
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
        ImageView btnCommentMenu;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnCommentMenu = itemView.findViewById(R.id.btnCommentMenu);
        }
    }
}
