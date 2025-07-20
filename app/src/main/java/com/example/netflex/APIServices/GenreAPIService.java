package com.example.netflex.APIServices;

import com.example.netflex.model.Genre;
import com.example.netflex.resonseAPI.GenreResponse;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GenreAPIService {
    @GET("api/genre")
    Call<GenreResponse> getGenres();
}

