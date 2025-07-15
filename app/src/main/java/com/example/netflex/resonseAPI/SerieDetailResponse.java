package com.example.netflex.resonseAPI;

import com.example.netflex.model.Episode;
import com.example.netflex.model.Serie;
import java.util.List;

public class SerieDetailResponse {
    private Serie serie;
    private List<Episode> episodes;

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
}
