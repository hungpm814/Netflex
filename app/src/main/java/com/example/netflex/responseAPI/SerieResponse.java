package com.example.netflex.responseAPI;

import com.example.netflex.model.Serie;

import java.util.List;

public class SerieResponse {
    public List<Serie> items;
    public int pageNumber;
    public int pageSize;
    public int totalItems;
    public int totalPages;
}
