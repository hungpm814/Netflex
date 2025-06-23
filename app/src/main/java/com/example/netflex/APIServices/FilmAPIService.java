package com.example.netflex.APIServices;

import com.example.netflex.resonseAPI.FilmResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FilmAPIService {
    @GET("api/film")
    Call<FilmResponse> getFilms();
}

