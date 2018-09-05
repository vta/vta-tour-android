package com.vta.virtualtour.models;

/**
 * Created by tushar on 17/04/18.
 */

public class Route {
    private String code;
    private String departure;
    private String destination;
    private String directionAName;
    private String directionBName;
    private String name;
    private double latitude;
    private double longitude;

    public Route() {
    }

    public Route(String code, String departure, String destination, String directionAName, String directionBName, String name, double latitude, double longitude) {
        this.code = code;
        this.departure = departure;
        this.destination = destination;
        this.directionAName = directionAName;
        this.directionBName = directionBName;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDirectionAName() {
        return directionAName;
    }

    public void setDirectionAName(String directionAName) {
        this.directionAName = directionAName;
    }

    public String getDirectionBName() {
        return directionBName;
    }

    public void setDirectionBName(String directionBName) {
        this.directionBName = directionBName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
