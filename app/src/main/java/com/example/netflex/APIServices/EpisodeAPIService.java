package com.example.netflex.APIServices;

import com.example.netflex.model.Episode;
import com.example.netflex.responseAPI.EpisodeDetailResponse;
import com.example.netflex.responseAPI.EpisodeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EpisodeAPIService {

    // API cho admin: có phân trang
    @GET("api/episode/serie/{serieId}")
    Call<EpisodeResponse> getEpisodes(
            @Path("serieId") String serieId,
            @Query("searchTerm") String searchTerm,
            @Query("sortOrder") String sortOrder,
            @Query("page") int page
    );

    // API cho Android: lấy tất cả tập phim trong một serie
    @GET("api/episode/serie/{serieId}")
    Call<List<Episode>> getAllEpisodes(@Path("serieId") String serieId);

    // Lấy chi tiết tập phim
    @GET("api/episode/{id}")
    Call<EpisodeDetailResponse> getEpisode(@Path("id") String episodeId);
}