package com.vta.virtualtour.ui.activities.nearMeCategoriesScreen;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.models.NearByCategory;

import java.util.ArrayList;

/**
 * Created by tushar
 * Created on 7/19/2018.
 */
public interface NearMeCategoriesContract {
    interface View {
        void showCategory(ArrayList<NearByCategory> categories);

        void showProgressBar();

        void hideProgressBar();
    }

    interface Presenter {
        void fetchPlaceOfInterestList(LatLng currentLocation);

        void fetchIntegrations();

        void showProgressBar();

        void hideProgressBar();

        void loadMorePOIS(LatLng latLng);
    }
}
