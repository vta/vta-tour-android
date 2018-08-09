package com.vta.virtualtour.models;

/**
 * Created by tushar on 21-Jul-18.
 */

public class NearByPlaceOfInterest {
    private String name;
    private String categoryType;

    public NearByPlaceOfInterest(String name, String categoryType) {
        this.name = name;
        this.categoryType = categoryType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }
}
