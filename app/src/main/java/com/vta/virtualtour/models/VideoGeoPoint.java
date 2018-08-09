package com.vta.virtualtour.models;

/**
 * Created by tushar on 17/04/18.
 */

public class VideoGeoPoint {
    private int sec;
    private double lat;
    private double lng;

    public VideoGeoPoint() {
    }

    public VideoGeoPoint(int sec, double lat, double lng) {
        this.sec = sec;
        this.lat = lat;
        this.lng = lng;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
