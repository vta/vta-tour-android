package com.vta.virtualtour.ui.activities.nearMeCustomPoiScreen;

import android.location.Location;

import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.MarkerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tushar Vengurlekar
 * Created on 30/07/18.
 */

public class NearMeCustomPoiPresenter implements NearMeCustomPoiContract.Presenter {

    private NearMeCustomPoiContract.View view;

    NearMeCustomPoiPresenter(NearMeCustomPoiContract.View view) {
        this.view = view;
    }

    @Override
    public void fetchCustomPoiList(final String latitude, final String longitude) {

        RouteManager.getSharedInstance().fetchCustomPoi(new RouteManager.FetchCustomPoiListener() {
            @Override
            public void didFinishFetchingCustomPois(List<MarkerInfo> customPois, String error) {
                List<MarkerInfo> customPOIs = filterPOIs(customPois, latitude, longitude);
                view.showCustomPoi(customPOIs);
            }
        });
    }

    private List<MarkerInfo> filterPOIs(List<MarkerInfo> inCustomPOIs, String latitude, String longitude) {
        List<MarkerInfo> customPOIs = new ArrayList<>();
        //TODO : Filter Routes by Lat/Long to show routes within 1 Mile Range Only
        for (int i = 0; i < inCustomPOIs.size(); i++) {
            float[] results = new float[1];
            Location.distanceBetween(inCustomPOIs.get(i).getLatitude(), inCustomPOIs.get(i).getLongitude(), Double.valueOf(latitude), Double.valueOf(longitude), results);
            float distanceInMeters = results[0];
            boolean isWithin10m = distanceInMeters < 1609;// Mile
            if (isWithin10m) {
                customPOIs.add(inCustomPOIs.get(i));
            }
        }
        return customPOIs;
    }

    @Override
    public void showProgressBar() {
        view.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        view.hideProgressBar();
    }
}
