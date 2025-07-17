package com.example.netflex.model;

import java.util.List;

public class Serie {
    private String id;
    private String title;
    private String poster;
    private String about;
    private String ageCategoryId;
    private int productionYear;
    private String trailer;
    private String path;

    private List<String> genres;
    private List<String> countries;
    private List<String> actors;

    // Getter & Setter for trailer
    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    // Getter & Setter for path
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // Getter & Setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter & Setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter & Setter for poster
    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    // Getter & Setter for about
    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    // Getter & Setter for ageCategoryId
    public String getAgeCategoryId() {
        return ageCategoryId;
    }

    public void setAgeCategoryId(String ageCategoryId) {
        this.ageCategoryId = ageCategoryId;
    }

    // Getter & Setter for productionYear
    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }
}
