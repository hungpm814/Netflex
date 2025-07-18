package com.example.netflex.model;

import java.util.UUID;

public class Genre {
    public UUID id;
    public String name;

    public UUID getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }

}
