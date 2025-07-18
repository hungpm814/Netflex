package com.example.netflex.model;

import java.util.UUID;

public class Country {
    public UUID id;
    public String name;

    @Override
    public String toString() {
        return name;
    }

}
