package com.vta.virtualtour.models;

/**
 * Created by tushar on 14/04/18.
 */

public class CommonSpinnerDataModel {
    private String id;
    private String name;
    private Route route;

    public CommonSpinnerDataModel(String id, String name, Route route) {
        this.id = id;
        this.name = name;
        this.route = route;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
