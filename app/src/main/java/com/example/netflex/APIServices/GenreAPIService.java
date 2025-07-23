package com.example.netflex.APIServices;

import com.example.netflex.model.Genre;
import com.example.netflex.responseAPI.GenreResponse;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GenreAPIService {
    @GET("api/genre")
    Call<GenreResponse> getGenres();
    @GET("api/genre/{id}")
    Call<Genre> getGenreById(@Path("id") UUID id);
}

