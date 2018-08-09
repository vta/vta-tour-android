package com.vta.virtualtour.ui.activities.navigationScreen;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

/**
 * Created by tushar
 * Created on 6/10/2018.
 */
public interface NavigationContract {

    interface View {
        void updateMapView(DirectionsResult result);

        void setDirections(String directions);
        void showFullscreenMapView();
    }

    interface Presenter {
        void getDirectionDetails(TravelMode mode);
        void fullscreenButtonClicked();
    }
}
