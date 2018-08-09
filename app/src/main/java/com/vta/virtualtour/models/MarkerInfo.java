package com.vta.virtualtour.models;

/**
 * Created by tushar on 21-Jun-18.
 */

public class MarkerInfo {
    private String title;
    private String subtitle;
    private double latitude;
    private double longitude;
    private String icon;

    public MarkerInfo(String title, String subtitle, double latitude, double longitude, String icon) {
        this.title = title;
        this.subtitle = subtitle;
        this.latitude = latitude;
        this.longitude = longitude;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
