package com.example.netflex.APIServices;

import com.example.netflex.responseAPI.FilmResponse;
import com.example.netflex.viewModels.FilmDetailViewModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FilmAPIService {
    @GET("api/film")
    Call<FilmResponse> getFilms();
    @GET("api/Film/{filmId}")
    Call<FilmDetailViewModel> getFilm(@Path("filmId") String filmId);
}

