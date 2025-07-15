package com.example.netflex.APIServices;

import com.example.netflex.model.Serie;
import com.example.netflex.resonseAPI.SerieDetailResponse;
import com.example.netflex.resonseAPI.SerieResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SerieAPIService {
    @GET("api/serie")
    Call<SerieResponse> getSeries(@Query("page") Integer page);

    @GET("api/serie/{id}")
    Call<SerieDetailResponse> getSerieDetail(@Path("id") String serieId);
}
