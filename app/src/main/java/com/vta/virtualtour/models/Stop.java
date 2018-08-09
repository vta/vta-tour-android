package com.vta.virtualtour.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tushar on 17/04/18.
 */

public class Stop implements Parcelable{
    private String code;
    private double lat;
    private double lng;
    private String name;
    private int sec;
    private String route_list;
    private String amenities;

    public Stop() {
    }

    public Stop(String code, double lat, double lng, String name, int sec, String route_list, String amenities) {
        this.code = code;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.sec = sec;
        this.route_list = route_list;
        this.amenities = amenities;
    }

    protected Stop(Parcel in) {
        code = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        name = in.readString();
        sec = in.readInt();
        route_list = in.readString();
        amenities = in.readString();
    }

    public static final Creator<Stop> CREATOR = new Creator<Stop>() {
        @Override
        public Stop createFromParcel(Parcel in) {
            return new Stop(in);
        }

        @Override
        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public String getRoute_list() {
        return route_list;
    }

    public void setRoute_list(String route_list) {
        this.route_list = route_list;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(name);
        dest.writeInt(sec);
        dest.writeString(route_list);
        dest.writeString(amenities);
    }
}
