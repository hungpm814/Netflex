package com.example.netflex.requestAPI.auth;

public class RatingRequest {
    private String createrId;
    private String serieId;
    private int rating;

    public RatingRequest(String createrId, String serieId, int rating) {
        this.createrId = createrId;
        this.serieId = serieId;
        this.rating = rating;
    }
}
