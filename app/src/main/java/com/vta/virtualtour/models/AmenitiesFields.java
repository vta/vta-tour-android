package com.vta.virtualtour.models;

/**
 * Created by tushar on 26-Jun-18.
 */

public class AmenitiesFields {
    private boolean enabled;
    private String key;
    private String value;

    public AmenitiesFields() {
    }

    public AmenitiesFields(boolean enabled, String key, String value) {
        this.enabled = enabled;
        this.key = key;
        this.value = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
