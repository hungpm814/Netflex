package com.example.netflex.resonseAPI;

import com.example.netflex.model.Episode;

import java.util.List;

public class EpisodeResponse {
    public List<Episode> episodes;
    public String serieTitle;
    public String serieId;
    public String searchTerm;
    public String sortOrder;
}
