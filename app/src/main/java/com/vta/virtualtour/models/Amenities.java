package com.vta.virtualtour.models;

import java.util.List;

/**
 * Created by tushar on 26-Jun-18.
 */

public class Amenities {
    private List<AmenitiesFields> list;
    private String stop_code;

    public Amenities() {
    }

    public Amenities(List<AmenitiesFields> list, String stop_code) {
        this.list = list;
        this.stop_code = stop_code;
    }

    public List<AmenitiesFields> getList() {
        return list;
    }

    public void setList(List<AmenitiesFields> list) {
        this.list = list;
    }

    public String getStop_code() {
        return stop_code;
    }

    public void setStop_code(String stop_code) {
        this.stop_code = stop_code;
    }
}
