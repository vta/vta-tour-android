package com.vta.virtualtour.ui.activities.nearMeCategoryDetailsScreen;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.Amenities;
import com.vta.virtualtour.models.AmenitiesFields;
import com.vta.virtualtour.models.MarkerInfo;
import com.vta.virtualtour.models.NearByCategory;
import com.vta.virtualtour.models.NearByPlaceOfInterest;
import com.vta.virtualtour.models.Stop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tushar on 20-07-2018.
 */

public class NearMeCategoryDetailsPresenter implements NearMeCategoryDetailsContract.Presenter {
    private NearMeCategoryDetailsContract.View view;

    public NearMeCategoryDetailsPresenter(NearMeCategoryDetailsContract.View view) {
        this.view = view;
    }

    @Override
    public void fetchAmenitiesForStop(Stop stop) {
        RouteManager.getSharedInstance().fetchAmenitiesForStop(stop, new RouteManager.FetchAmenitiesForStopListener() {
            @Override
            public void didFinishFetchingAmenitiesForStop(Amenities amenities, String error) {
                List<String> amenitiesList = new ArrayList<String>();
                for (AmenitiesFields amenitiesFields : amenities.getList()) {
                    // Amenities are added only if the key is not "stop_id" and value is not 0
                    if (!amenitiesFields.getKey().equals("stop_id") && !amenitiesFields.getValue().equals("0") && !amenitiesFields.getValue().equals("")) {
                        String amenitiesText;
                        // Only key is appended if value = 1 else key: value
                        if (amenitiesFields.getValue().equals("1")) {
                            amenitiesList.add(amenitiesFields.getKey());
                        } else {
                            amenitiesList.add(amenitiesFields.getKey() + ": " + amenitiesFields.getValue());
                        }
                    }
                }
                view.reloadRecyclerView(amenitiesList);
            }
        });
    }

    @Override
    public void fetchConnectionsForStop(Stop stop) {
        List<String> connectionList = new ArrayList<>();
        connectionList.add(stop.getRoute_list());
        view.reloadRecyclerView(connectionList);
    }

    @Override
    public void fetchSocialGatheringForStop(Stop stop) {
        RouteManager.getSharedInstance().fetchMeetups(new LatLng(stop.getLat(), stop.getLng()), new RouteManager.FetchMeetupsListener() {
            @Override
            public void didFinishFetchingMeetups(List<MarkerInfo> meetups, String error) {
                List<String> meetsUpList = new ArrayList<String>();
                for (MarkerInfo markerInfo : meetups) {
                    meetsUpList.add(markerInfo.getTitle());
                }
                view.reloadRecyclerView(meetsUpList);
            }
        });
    }

    @Override
    public void fetchPlaceOfInterestList(NearByCategory nearByCategory) {
        List<String> placeOfInterestList = new ArrayList<>();
        for (NearByPlaceOfInterest nearByPlaceOfInterest : RouteManager.getSharedInstance().getNearByPlaceOfInterests()) {
            if (nearByPlaceOfInterest.getCategoryType().equals(nearByCategory.getKey()))
                placeOfInterestList.add(nearByPlaceOfInterest.getName());
        }
        view.reloadRecyclerView(placeOfInterestList);
    }
}
