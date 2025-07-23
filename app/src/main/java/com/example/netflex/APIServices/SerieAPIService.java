package com.example.netflex.APIServices;

import com.example.netflex.model.Serie;
import com.example.netflex.responseAPI.SerieDetailResponse;
import com.example.netflex.responseAPI.SerieResponse;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SerieAPIService {
    @GET("api/serie")
    Call<SerieResponse> getSeries(@Query("page") Integer page);

    @GET("api/serie/filter")
    Call<SerieResponse> getSeriesFilter(
            @Query("page") Integer page,
            @Query("genreId") UUID genreId,
            @Query("countryId") UUID countryId,
            @Query("year") Integer year,
            @Query("keyword") String keyword
    );

    @GET("api/serie/{id}")
    Call<SerieDetailResponse> getSerieDetail(@Path("id") String serieId);
}
