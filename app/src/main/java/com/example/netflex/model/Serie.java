package com.example.netflex.model;

public class Serie {
    private String id;
    private String title;
    private String poster;
    private String about;
    private String ageCategoryId;
    private int productionYear;

    public String trailer;

    private String path; // video preview hoặc tập đầu tiên

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public String getAgeCategoryId() {
        return ageCategoryId;
    }

    public void setAgeCategoryId(String ageCategoryId) {
        this.ageCategoryId = ageCategoryId;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
