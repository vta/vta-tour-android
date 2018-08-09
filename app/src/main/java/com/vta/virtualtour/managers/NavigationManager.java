package com.vta.virtualtour.managers;

import android.content.Context;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.vta.virtualtour.R;
import com.vta.virtualtour.VirtualTourApplication;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by tushar
 * Created on 6/10/2018.
 */
public class NavigationManager {

    private static NavigationManager navigationManager;
    private Context context;
    //    private DirectionsStep[] directionsSteps;
    private List<DirectionsStep> directionsStepList = new ArrayList<>();

    public static NavigationManager getSharedInstance() {
        if (navigationManager == null) {
            navigationManager = new NavigationManager();
            navigationManager.context = VirtualTourApplication.getAppContext();
        }
        return navigationManager;
    }

    public DirectionsResult getDirectionsDetails(LatLng origin, LatLng destination, TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*public DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(context.getResources().getString(R.string.api_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    /*public DirectionsStep[] getDirectionsSteps() {
        return directionsSteps;
    }

    public void setDirectionsSteps(DirectionsStep[] directionsSteps) {
        this.directionsSteps = directionsSteps;
    }*/

    public List<DirectionsStep> getDirectionsStepList() {
        return directionsStepList;
    }
}
