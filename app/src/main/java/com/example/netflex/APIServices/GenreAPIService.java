package com.example.netflex.APIServices;

import com.example.netflex.responseAPI.GenreResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GenreAPIService {
    @GET("api/genre")
    Call<GenreResponse> getGenres();
}

