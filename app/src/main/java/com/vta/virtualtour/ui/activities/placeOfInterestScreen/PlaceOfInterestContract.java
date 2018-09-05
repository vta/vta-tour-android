package com.vta.virtualtour.ui.activities.placeOfInterestScreen;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.models.PlaceOfInterest;

import java.util.ArrayList;

/**
 * Created by tushar
 * Created on 13/04/18.
 */

public interface PlaceOfInterestContract {
    interface View {
        Context getContext();

        void reloadPlaceOfInterestList(ArrayList<PlaceOfInterest> placeOfInterestList, String nextPageToken);
    }

    interface Presenter {
        void fetchPlaceOfInterestList(LatLng currentLocation, String poiType, String token);
    }
}
