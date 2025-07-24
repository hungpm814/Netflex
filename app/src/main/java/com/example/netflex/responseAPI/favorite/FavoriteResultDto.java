package com.example.netflex.responseAPI.favorite;
import com.example.netflex.responseAPI.favorite.FavoriteFilmDto;
import com.example.netflex.responseAPI.favorite.FavoriteSeriesDto;


import java.util.List;

public class FavoriteResultDto {
    private String userId;
    private List<FavoriteFilmDto> favoriteFilms;
    private List<FavoriteSeriesDto> favoriteSeries;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<FavoriteFilmDto> getFavoriteFilms() {
        return favoriteFilms;
    }

    public void setFavoriteFilms(List<FavoriteFilmDto> favoriteFilms) {
        this.favoriteFilms = favoriteFilms;
    }

    public List<FavoriteSeriesDto> getFavoriteSeries() {
        return favoriteSeries;
    }

    public void setFavoriteSeries(List<FavoriteSeriesDto> favoriteSeries) {
        this.favoriteSeries = favoriteSeries;
    }
}
