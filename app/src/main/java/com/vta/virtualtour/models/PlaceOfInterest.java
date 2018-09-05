package com.vta.virtualtour.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by tushar
 * Created on 13/04/18.
 */

public class PlaceOfInterest implements Parcelable {
    private String id;
    private String name;
    private String availability;
    private String address;
    private String phone;
    private String website;
    private String imageUrl;
    private LatLng currentLatLong;
    private LatLng placeLatLong;
    private float ratings;
    private ArrayList<String> types = new ArrayList<>();

    public PlaceOfInterest() {
    }

    public PlaceOfInterest(String id, String name, String availability, String address, String phone, String website, String imageUrl, LatLng currentLatLong, LatLng placeLatLong, float ratings, ArrayList<String> types) {
        this.id = id;
        this.name = name;
        this.availability = availability;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.imageUrl = imageUrl;
        this.currentLatLong = currentLatLong;
        this.placeLatLong = placeLatLong;
        this.ratings = ratings;
        this.types = types;
    }

    protected PlaceOfInterest(Parcel in) {
        id = in.readString();
        name = in.readString();
        availability = in.readString();
        address = in.readString();
        phone = in.readString();
        website = in.readString();
        imageUrl = in.readString();
        currentLatLong = in.readParcelable(LatLng.class.getClassLoader());
        placeLatLong = in.readParcelable(LatLng.class.getClassLoader());
        ratings = in.readFloat();
        types = in.createStringArrayList();
    }

    public static final Creator<PlaceOfInterest> CREATOR = new Creator<PlaceOfInterest>() {
        @Override
        public PlaceOfInterest createFromParcel(Parcel in) {
            return new PlaceOfInterest(in);
        }

        @Override
        public PlaceOfInterest[] newArray(int size) {
            return new PlaceOfInterest[size];
        }
    };

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

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LatLng getCurrentLatLong() {
        return currentLatLong;
    }

    public void setCurrentLatLong(LatLng currentLatLong) {
        this.currentLatLong = currentLatLong;
    }

    public LatLng getPlaceLatLong() {
        return placeLatLong;
    }

    public void setPlaceLatLong(LatLng placeLatLong) {
        this.placeLatLong = placeLatLong;
    }

    public float getRatings() {
        return ratings;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(availability);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(website);
        dest.writeString(imageUrl);
        dest.writeParcelable(currentLatLong, flags);
        dest.writeParcelable(placeLatLong, flags);
        dest.writeFloat(ratings);
        dest.writeStringList(types);
    }
}
