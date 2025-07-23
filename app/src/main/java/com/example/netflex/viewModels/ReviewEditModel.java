package com.example.netflex.viewModels;

public class ReviewEditModel {
    private int rating;
    private String filmId;
    private String serieId;
    private String createrId;
    public ReviewEditModel(int rating, String serieId, String createrId) {
        this.rating = rating;
        this.serieId = serieId;
        this.filmId = null;
        this.createrId = createrId;
    }

    public ReviewEditModel() {

    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public String getSerieId() {
        return serieId;
    }

    public void setSerieId(String serieId) {
        this.serieId = serieId;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }
}
