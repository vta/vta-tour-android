package com.vta.virtualtour.ui.activities.placeOfInterestScreen;

import android.content.Context;

import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.models.PlaceOfInterest;
import com.vta.virtualtour.models.PlaceOfInterestCategory;

import java.util.ArrayList;

/**
 * Created by tushar on 13/04/18.
 */

public interface PlaceOfInterestContract {
    interface View {
        Context getContext();
        void setupLocationManager();
        void showCategory(ArrayList<PlaceOfInterestCategory> categories);
        void reloadPlaceOfInterestList(ArrayList<PlaceOfInterest> placeOfInterestList);
    }

    interface Presenter {
        void fetchCurrentLocation();
        void fetchPlaceOfInterestList(LatLng currentLocation,boolean fromNearMe);
    }
}
