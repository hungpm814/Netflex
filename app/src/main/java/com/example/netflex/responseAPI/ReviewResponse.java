package com.example.netflex.responseAPI;

public class ReviewResponse {
    private String message;
    private double averageRating;
    private int totalReviews;
    private String serieId;

    private String filmId;


    // Getter & Setter
    public double getAverageRating() {
        return averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public String getSerieId() {
        return serieId;
    }

    public String getMessage() {
        return message;
    }

    public String getFilmId(){
        return  filmId;
    }

}

