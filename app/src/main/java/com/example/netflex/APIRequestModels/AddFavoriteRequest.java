package com.example.netflex.APIRequestModels;

public class AddFavoriteRequest {
    private String userId;
    private String filmId;
    private String seriesId;

    public String getUserId() {
        return userId;
    }

    public AddFavoriteRequest(String userId, String filmId, String seriesId) {
        this.userId = userId;
        this.filmId = filmId;
        this.seriesId = seriesId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }
}
