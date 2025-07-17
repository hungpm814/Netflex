package com.example.netflex.responseAPI;

import com.example.netflex.model.Actor;
import com.example.netflex.model.Episode;
import com.example.netflex.model.Genre;
import com.example.netflex.model.Serie;

import java.util.List;

public class SerieDetailResponse {
    private Serie serie;
    private List<Episode> episodes;
    private List<String> countries;
    private List<Genre> genres;

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    private List<Actor> actors;

    // Getters
    public Serie getSerie() {
        return serie;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public List<String> getCountries() {
        return countries;
    }

    public List<Actor> getActors() {
        return actors;
    }

    // Setters
    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }
}
