package com.example.netflex.APIServices;

import com.example.netflex.APIRequestModels.PostCommentRequest;
import com.example.netflex.model.Comment;
import com.example.netflex.responseAPI.CommentListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentAPIService {
    @GET("api/comments/paged/{filmId}")
    Call<CommentListResponse> getComments(@Path("filmId") String filmId,
                                          @Query("page") int page,
                                          @Query("pageSize") int pageSize,
                                          @Query("sort") String sort);
    @POST("api/comments")
    Call<Boolean> postComment(@Body PostCommentRequest request);
}
