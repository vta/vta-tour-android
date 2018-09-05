package com.vta.virtualtour.managers;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.vta.virtualtour.R;
import com.vta.virtualtour.VirtualTourApplication;
import com.vta.virtualtour.interfaces.AsyncTaskJsonResultListener;
import com.vta.virtualtour.utility.JSONAsyncTask;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by tushar
 * Created on 6/10/2018.
 */
public class NavigationManager {

    private static NavigationManager navigationManager;
    private static GeoApiContext geoApiContext;

    public static NavigationManager getSharedInstance() {
        if (navigationManager == null) {
            navigationManager = new NavigationManager();
            geoApiContext = getGeoContext();
        }
        return navigationManager;
    }

    public interface GetNavigationListener {
        void didFinishGetNavigation(String direction, String error);
    }

    public void getNavigation(LatLng origin, LatLng destination, final GetNavigationListener listener) {

        new JSONAsyncTask(new AsyncTaskJsonResultListener() {
            @Override
            public void asyncTaskResult(JSONObject jsonObject) {
                try {
                    JSONArray routesArray = jsonObject.getJSONArray("routes");
                    if (routesArray.length() > 0) {
                        JSONObject object = routesArray.getJSONObject(0);
                        JSONArray legsArray = object.getJSONArray("legs");
                        if (legsArray.length() > 0) {
                            JSONObject legsObject = legsArray.getJSONObject(0);
                            JSONArray stepsArray = legsObject.getJSONArray("steps");
                            stepsArray = stepsArray == null ? new JSONArray() : stepsArray;

                            if (stepsArray.length() > 0) {

                                JSONObject step = stepsArray.getJSONObject(0);
                                String instruction = (String) step.get("html_instructions");

                                listener.didFinishGetNavigation(instruction, "");
                            } else {
                                listener.didFinishGetNavigation("", "Unknown Error");

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    listener.didFinishGetNavigation("", e.getLocalizedMessage());
                }
            }
        }).execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude + "&destination=" + destination.latitude + "," + destination.longitude + "&key=" + VirtualTourApplication.getAppContext().getResources().getString(R.string.api_key));
    }


    private static GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(VirtualTourApplication.getAppContext().getResources().getString(R.string.api_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS)
                .setMaxRetries(0)
                .setRetryTimeout(1, TimeUnit.SECONDS);
    }

    public DirectionsResult getDirectionsDetails(LatLng origin, LatLng destination, TravelMode mode) {

        com.google.maps.model.LatLng origin1 = new com.google.maps.model.LatLng(origin.latitude, origin.longitude);
        com.google.maps.model.LatLng destination1 = new com.google.maps.model.LatLng(destination.latitude, destination.longitude);

        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(geoApiContext)
                    .mode(mode)
                    .origin(origin1)
                    .destination(destination1)
                    .departureTime(now)
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
