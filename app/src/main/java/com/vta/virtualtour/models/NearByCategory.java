package com.vta.virtualtour.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tushar on 20/07/18.
 */

public class NearByCategory implements Parcelable {
    private String key;
    private String name;
    private boolean isPoiCategory;

    public NearByCategory(String key, String name, boolean isPoiCategory) {
        this.key = key;
        this.name = name;
        this.isPoiCategory = isPoiCategory;
    }

    protected NearByCategory(Parcel in) {
        key = in.readString();
        name = in.readString();
        isPoiCategory = in.readByte() != 0;
    }

    public static final Creator<NearByCategory> CREATOR = new Creator<NearByCategory>() {
        @Override
        public NearByCategory createFromParcel(Parcel in) {
            return new NearByCategory(in);
        }

        @Override
        public NearByCategory[] newArray(int size) {
            return new NearByCategory[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPoiCategory() {
        return isPoiCategory;
    }

    public void setPoiCategory(boolean poiCategory) {
        isPoiCategory = poiCategory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeByte((byte) (isPoiCategory ? 1 : 0));
    }
}
