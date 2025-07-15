package com.example.netflex.model;

public class Episode {
    public String id;
    public String title;
    public String description;
    public String year;
    public String poster;
    public String path;
    public String trailer;

    // Getter methods
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getYear() {
        return year;
    }

    public String getPoster() {
        return poster;
    }

    public String getPath() {
        return path;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    // Optional: Constructor
    public Episode(String id, String title, String description, String year, String poster, String path, String trailer) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.year = year;
        this.poster = poster;
        this.path = path;
        this.trailer = trailer;
    }

    public Episode() {
        // empty constructor for deserialization
    }
}
