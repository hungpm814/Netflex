package com.example.netflex.viewModels;

import com.example.netflex.model.Actor;
import com.example.netflex.model.Film;

import java.util.List;

public class FilmDetailViewModel {
    public Film film;
    public List<Actor> actors;

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
}
