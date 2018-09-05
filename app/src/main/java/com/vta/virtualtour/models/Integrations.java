package com.vta.virtualtour.models;

/**
 * Created by tushar
 * Created on 21-Jun-18.
 */

public class Integrations {
    private String coordApiKey;
    private String meetUpApiKey;
    private String MultiCycleApiKey;


    public Integrations() {
    }

    public Integrations(String coordApiKey, String meetUpApiKey, String MultiCycleApiKey) {
        this.coordApiKey = coordApiKey;
        this.meetUpApiKey = meetUpApiKey;
        this.MultiCycleApiKey = MultiCycleApiKey;
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

    public String getMultiCycleApiKey() {
        return MultiCycleApiKey;
    }

    public void setMultiCycleApiKey(String multiCycleApiKey) {
        MultiCycleApiKey = multiCycleApiKey;
    }
}
