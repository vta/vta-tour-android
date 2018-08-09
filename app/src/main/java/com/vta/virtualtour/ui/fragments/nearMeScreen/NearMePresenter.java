package com.vta.virtualtour.ui.fragments.nearMeScreen;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.Route;
import com.vta.virtualtour.models.RouteDetails;
import com.vta.virtualtour.models.Stop;
import com.vta.virtualtour.utility.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tushar on 7/18/2018.
 */
public class NearMePresenter implements NearMeContract.Presenter {

    private NearMeContract.View view;
    private static final int ROUTE_CHOOSER_DISTANCE = 4000;

    public NearMePresenter(NearMeContract.View view) {
        this.view = view;
    }

    @Override
    public void fetchRoutes(final String latitude, final String longitude) {
        view.showProgressBarLayout();
        RouteManager.getSharedInstance().fetchRoutes(new RouteManager.FetchRoutesListener() {
            @Override
            public void didFinishFetchingRoutes(List<Route> routes, String error) {
                view.hideProgressBarLayout();
                if (routes != null) {
                    view.enableRoutesTextView(true);
                    view.reloadRoutes(RouteManager.getSharedInstance().getCustomNearMeRoutes(filterRoutes(routes, latitude, longitude)));
                }
            }
        });
    }

    @Override
    public void populateDirections(int position) {
        view.enableDirectionSpinner(true);
        view.reloadDirections(RouteManager.getSharedInstance().getCustomNearByDirections(position));
    }

    @Override
    public void onRouteSelected() {
        view.hideSoftKeyboard();
    }

    @Override
    public void fetchRouteDetails(int position, final LatLng currentLocation) {
        view.showProgressBarLayout();
        RouteManager.getSharedInstance().fetchRouteDetails(position, new RouteManager.FetchRouteDetailsListener() {
            @Override
            public void didFinishFetchingRouteDetails(RouteDetails routeDetails, String error) {
                view.hideProgressBarLayout();
                if (routeDetails != null){
                    if (routeDetails.getStops().size() != 0){
                        RouteManager.getSharedInstance().setNearMeStopList(getFilteredNearMeStops(routeDetails.getStops(),String.valueOf(currentLocation.latitude),String.valueOf(currentLocation.longitude)));
                    }
                }
            }
        });
    }

    @Override
    public void clearRouteChooserFields() {
        view.clearRouteChooserFields();
        view.hideSoftKeyboard();
        RouteManager.getSharedInstance().setNearMeRoute(null);
//        RouteManager.getSharedInstance().clearRouteChooserFields();
    }

    @Override
    public void checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(view.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) view.getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSION_REQUEST_CODE);
        } else {
            view.locationPermissionGranted();

        }
    }

    @Override
    public void fetchCurrentLocation() {
        view.setupLocationManager();
    }

    private List<Route> filterRoutes(List<Route> routesIn, String latitude, String longitude) {
        List<Route> routes = new ArrayList<>();
        //TODO : Filter Routes by Lat/Long to show routes within 10 Meter Range Only
        for (int i = 0; i < routesIn.size(); i++) {
            float[] results = new float[1];
            Location.distanceBetween(routesIn.get(i).getLatitude(), routesIn.get(i).getLongitude(), Double.valueOf(latitude), Double.valueOf(longitude), results);
            float distanceInMeters = results[0];
            boolean isWithin10m = distanceInMeters < ROUTE_CHOOSER_DISTANCE;
            if (isWithin10m) {
                routes.add(routesIn.get(i));
            }
        }
        return routes;
    }

    private List<Stop> getFilteredNearMeStops(List<Stop> stopsIn, String latitude, String longitude) {
        List<Stop> filteredStops = new ArrayList<>();
        ArrayList<Float> distanceArray = new ArrayList<>();
        for (int i = 0; i < stopsIn.size(); i++) {
            float[] results = new float[1];
            Location.distanceBetween(stopsIn.get(i).getLat(), stopsIn.get(i).getLng(), Double.valueOf(latitude), Double.valueOf(longitude), results);
            distanceArray.add(results[0]);
        }

        for (int j = 0; j < 3; j++) {
            int smallestIndex = 0;
            float smallest = distanceArray.get(0);
            for (int i = 0; i < distanceArray.size(); i++) {
                if (distanceArray.get(i) < smallest) {
                    smallest = distanceArray.get(i);
                    smallestIndex = i;
                }
            }
            filteredStops.add(stopsIn.get(smallestIndex));
            distanceArray.remove(smallestIndex);
            stopsIn.remove(smallestIndex);
        }
        return filteredStops;
    }
}
