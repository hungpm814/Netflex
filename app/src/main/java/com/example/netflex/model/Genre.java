package com.example.netflex.model;

import java.util.UUID;

public class Genre {
    public UUID id;
    public String name;

    @Override
    public String toString() {
        return name;
    }

}