package com.vta.virtualtour.ui.fragments.nearMeScreen;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.models.Route;
import com.vta.virtualtour.models.Stop;

import java.util.List;

/**
 * Created by tushar on 7/18/2018.
 */
public interface NearMeContract {
    interface View {
        Context getContext();
        void showProgressBarLayout();
        void hideProgressBarLayout();
        void enableRoutesTextView(boolean shouldEnable);
        void reloadRoutes(List<Route> routes);
        void enableDirectionSpinner(boolean shouldEnable);
        void reloadDirections(List<String> directions);
        void hideSoftKeyboard();
        void clearRouteChooserFields();
        void locationPermissionGranted();
        void setupLocationManager();
    }

    interface Presenter {
        void fetchRoutes(String latitude, String longitude);
        void populateDirections(int position);
        void onRouteSelected();
        void fetchRouteDetails(int position, LatLng currentLocation);
        void clearRouteChooserFields();
        void checkForLocationPermission();
        void fetchCurrentLocation();
    }
}
