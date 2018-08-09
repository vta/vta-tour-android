package com.vta.virtualtour.models;

/**
 * Created by tushar on 21-Jun-18.
 */

public class Integrations {
    private String coordApiKey;
    private String meetUpApiKey;

    public Integrations() {
    }

    public Integrations(String coordApiKey, String meetUpApiKey) {
        this.coordApiKey = coordApiKey;
        this.meetUpApiKey = meetUpApiKey;
    }

    public String getCoordApiKey() {
        return coordApiKey;
    }

    public void setCoordApiKey(String coordApiKey) {
        this.coordApiKey = coordApiKey;
    }

    public String getMeetUpApiKey() {
        return meetUpApiKey;
    }

    public void setMeetUpApiKey(String meetUpApiKey) {
        this.meetUpApiKey = meetUpApiKey;
    }
}
