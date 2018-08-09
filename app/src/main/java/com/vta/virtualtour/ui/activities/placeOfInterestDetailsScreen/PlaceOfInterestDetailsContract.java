package com.vta.virtualtour.ui.activities.placeOfInterestDetailsScreen;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.models.PlaceOfInterest;

/**
 * Created by tushar on 16/04/18.
 */

public interface PlaceOfInterestDetailsContract {
    interface View {
        Context getContext();
        void showMapNavigation();
//        void isCallPermissionGranted(boolean isPermissionGranted);
        void showPhoneNumberDial();
        void openWebsite();
        void displayPoiDetails(PlaceOfInterest placeOfInterest);
        void showProgressBar();
        void showDistanceTimeToPoi(String distanceTimeString);
    }

    interface Presenter {
        void onMapNavigationButtonClick();
        void onPhoneNumberButtonClick();
        void callPhoneNumber();
        void onWebsiteButtonClick();
        void fetchPoiDetails(String placeId,LatLng currentLatLng,LatLng placeLatLng, String photoUrl);
        void fetchDistanceTimeToPoi(LatLng currentLocation, LatLng poiLocation);
    }
}
