package com.vta.virtualtour.ui.fragments.virtualTourScreen;

import android.content.Context;

import java.util.List;

/**
 * Created by Tushar on 7/18/2018.
 */
public interface VirtualTourContract {
    interface View {
        Context getContext();
        void reloadRoutes(List<String> routes);
        void reloadDirections(List<String> directions);
        void reloadDepartures(List<String> departures);
        void reloadDestinations(List<String> destinations);
        void setupLocationManager();
        void enableRoutesTextView(boolean shouldEnable);
        void enableDirectionSpinner(boolean shouldEnable);
        void enableDepartureSpinner(boolean shouldEnable);
        void enableDestinationSpinner(boolean shouldEnable);
        void hideSoftKeyboard();
        void showProgressBarLayout();
        void hideProgressBarLayout();
        void clearRouteChooserFields();
        void locationPermissionGranted();
    }

    interface Presenter {
        void fetchRoutes();
        void fetchRoutes(String latitude, String longitude);
        void fetchCurrentLocation();
        void fetchRouteDetails(int position);
        void fetchRouteDetailsVideoGeoPoints(String videoViewType, int position);
        void populateDirections(int position);
        void setDeparture(int position);
        void setDestination(int position);
        void onRouteSelected();
        void clearRouteChooserFields();
        void checkForLocationPermission();
    }
}
