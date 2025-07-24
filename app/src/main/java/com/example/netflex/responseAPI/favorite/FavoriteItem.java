package com.example.netflex.responseAPI.favorite;

public class FavoriteItem {
    private String id;
    private String title;
    private String poster;
    private boolean isFilm;

    public FavoriteItem(String id, String title, String poster, boolean isFilm) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.isFilm = isFilm;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPoster() { return poster; }
    public boolean isFilm() { return isFilm; }
}
