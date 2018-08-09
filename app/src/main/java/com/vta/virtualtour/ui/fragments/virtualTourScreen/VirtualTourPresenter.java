package com.vta.virtualtour.ui.fragments.virtualTourScreen;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.Route;
import com.vta.virtualtour.models.RouteDetails;
import com.vta.virtualtour.models.VideoGeoPoint;
import com.vta.virtualtour.utility.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tushar on 7/18/2018.
 */
public class VirtualTourPresenter implements VirtualTourContract.Presenter {

    private VirtualTourContract.View view;
    //TODO: Make it 10 later
    private static final int ROUTE_CHOOSER_DISTANCE = 1000;

    public VirtualTourPresenter(VirtualTourContract.View view) {
        this.view = view;
    }

    @Override
    public void fetchRoutes() {
        view.showProgressBarLayout();
        RouteManager.getSharedInstance().fetchRoutes(new RouteManager.FetchRoutesListener() {
            @Override
            public void didFinishFetchingRoutes(List<Route> routes, String error) {
                view.hideProgressBarLayout();
                if (routes != null) {
                    view.enableRoutesTextView(true);
                    view.reloadRoutes(RouteManager.getSharedInstance().getCustomRoutes(routes));
                }
            }
        });
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
                    view.reloadRoutes(RouteManager.getSharedInstance().getCustomRoutes(filterRoutes(routes, latitude, longitude)));
                }
            }
        });
    }

    @Override
    public void fetchRouteDetails(int position) {
        view.showProgressBarLayout();
        RouteManager.getSharedInstance().fetchRouteDetails(position, new RouteManager.FetchRouteDetailsListener() {
            @Override
            public void didFinishFetchingRouteDetails(RouteDetails routeDetails, String error) {
                RouteManager.getSharedInstance().setRouteDetails(routeDetails);

                view.hideProgressBarLayout();
                if (routeDetails != null) {
                    view.enableDepartureSpinner(true);
                    view.enableDestinationSpinner(true);
                    view.reloadDepartures(RouteManager.getSharedInstance().getCustomStops(routeDetails));
                    view.reloadDestinations(RouteManager.getSharedInstance().getCustomStops(routeDetails));
                }
            }
        });
    }

    @Override
    public void fetchRouteDetailsVideoGeoPoints(String videoViewType, int position) {
        RouteManager.getSharedInstance().fetchRouteDetailsVideoGeoPoints(videoViewType, position, new RouteManager.FetchRouteDetailsVideoGeoPointsListener() {
            @Override
            public void didFinishFetchingRouteDetailsVideoGeoPoints(List<VideoGeoPoint> videoGeoPoints, String error) {

            }
        });
    }

    @Override
    public void populateDirections(int position) {
        view.enableDirectionSpinner(true);
        view.reloadDirections(RouteManager.getSharedInstance().getCustomDirections(position));
    }

    @Override
    public void setDeparture(int position) {
        RouteManager.getSharedInstance().setDeparture(position);
    }

    @Override
    public void setDestination(int position) {
        RouteManager.getSharedInstance().setDestination(position);
    }

    @Override
    public void onRouteSelected() {
        view.hideSoftKeyboard();
    }

    @Override
    public void clearRouteChooserFields() {
        view.clearRouteChooserFields();
        view.hideSoftKeyboard();
        RouteManager.getSharedInstance().clearRouteChooserFields();
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
}
