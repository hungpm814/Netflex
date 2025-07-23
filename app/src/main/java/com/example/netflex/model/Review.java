package com.example.netflex.model;

public class Review {
    private int rating;
    private double averageRating;
    private int totalReviews;
    private String filmId;
    private String serieId;

    // Getters & Setters
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

    public String getFilmId() { return filmId; }
    public void setFilmId(String filmId) { this.filmId = filmId; }

    public String getSerieId() { return serieId; }
    public void setSerieId(String serieId) { this.serieId = serieId; }
}
