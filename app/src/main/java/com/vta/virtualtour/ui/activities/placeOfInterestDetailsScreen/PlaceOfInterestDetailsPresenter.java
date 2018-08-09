package com.vta.virtualtour.ui.activities.placeOfInterestDetailsScreen;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.PlaceOfInterest;

import java.text.DecimalFormat;

/**
 * Created by tushar on 16/04/18.
 */

public class PlaceOfInterestDetailsPresenter implements PlaceOfInterestDetailsContract.Presenter {
    private PlaceOfInterestDetailsContract.View view;

    public PlaceOfInterestDetailsPresenter(PlaceOfInterestDetailsContract.View view) {
        this.view = view;
    }

    @Override
    public void onMapNavigationButtonClick() {
        view.showMapNavigation();
    }

    @Override
    public void onPhoneNumberButtonClick() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (view.getContext().checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                callPhoneNumber();
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            callPhoneNumber();
        }
    }

    @Override
    public void callPhoneNumber() {
        view.showPhoneNumberDial();
    }

    @Override
    public void onWebsiteButtonClick() {
        view.openWebsite();
    }

    @Override
    public void fetchPoiDetails(String placeId, LatLng currentLatLng,LatLng placeLatLng, String photoUrl) {
        view.showProgressBar();
        RouteManager.getSharedInstance().fetchPlaceDetails(placeId, photoUrl, currentLatLng, placeLatLng, new RouteManager.FetchPlaceDetailsListener() {
            @Override
            public void didFinishFetchingPlacesDetails(PlaceOfInterest placeOfInterest, String error) {
                view.displayPoiDetails(placeOfInterest);
            }
        });
    }

    @Override
    public void fetchDistanceTimeToPoi(LatLng currentLocation, LatLng poiLocation) {
        RouteManager.getSharedInstance().fetchDistanceTimeToPoi(currentLocation, poiLocation, new RouteManager.FetchDistanceTimeToPoiListener() {
            @Override
            public void didFinishFetchDistanceTimeToPoi(String distanceInmetre, String timeInMinutes, String error) {
                view.showDistanceTimeToPoi(createDistanceTimeString(distanceInmetre,timeInMinutes));
            }
        });
    }

    private String createDistanceTimeString(String distanceInmetre, String timeInMinutes){
        //converts metres into miles
        int meter = Integer.parseInt(distanceInmetre);
        double miles = meter * 0.00062137119;

        DecimalFormat df = new DecimalFormat("#.##");
        miles = Double.valueOf(df.format(miles));

        return miles+" mi - "+timeInMinutes;
   }
}
