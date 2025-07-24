package com.example.netflex.APIServices;

import com.example.netflex.APIRequestModels.AddFavoriteRequest;
import com.example.netflex.responseAPI.favorite.FavoriteMessageResponse;
import com.example.netflex.responseAPI.favorite.FavoriteResultDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FavoriteApiService {

    // 1. Kiểm tra xem người dùng đã yêu thích film/series chưa
    @GET("api/favorite/is-favorite")
    Call<Boolean> isFavorite(
            @Query("userId") String userId,
            @Query("filmId") String filmId,
            @Query("seriesId") String seriesId
    );

    // 2. Thêm film/series vào danh sách yêu thích
    @POST("api/favorite")
    Call<FavoriteMessageResponse> addFavorite(@Body AddFavoriteRequest request);

    // 3. Xoá film/series khỏi danh sách yêu thích
    @DELETE("api/favorite/remove")
    Call<FavoriteMessageResponse> removeFavorite(
            @Query("userId") String userId,
            @Query("filmId") String filmId,
            @Query("seriesId") String seriesId
    );


    // 4. Lấy danh sách các mục yêu thích của user
    @GET("api/favorite/user/{userId}")
    Call<FavoriteResultDto> getFavoritesByUser(@Path("userId") String userId);

}
