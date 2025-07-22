package com.example.netflex.responseAPI;

import com.example.netflex.model.Comment;

import java.util.List;

public class CommentListResponse {
    public List<Comment> comments;
    public int totalPages;
    public int currentPage;
    public boolean hasMore;
}
