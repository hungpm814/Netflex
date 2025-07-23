package com.example.netflex.viewModels;

import com.example.netflex.model.Actor;
import com.example.netflex.model.Comment;
import com.example.netflex.model.Film;
import com.example.netflex.model.Genre;

import java.util.List;

public class FilmDetailViewModel {
    public Film film;
    public List<Actor> actors;
    public List<Comment> comments;
    private List<String> countries;
    private List<Genre> genres;

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
