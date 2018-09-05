package com.vta.virtualtour.models;

/**
 * Created by tushar
 * Created on 23/04/18.
 */

public class PlaceOfInterestCategory {
    private String name;
    private String key;

    public PlaceOfInterestCategory(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}