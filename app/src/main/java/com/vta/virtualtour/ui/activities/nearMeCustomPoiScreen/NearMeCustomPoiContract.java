package com.vta.virtualtour.ui.activities.nearMeCustomPoiScreen;

import com.vta.virtualtour.models.MarkerInfo;

import java.util.List;

/**
 * Created by sidhesh.naik on 30/07/18.
 */

public class NearMeCustomPoiContract {
    interface View {
        void showCustomPoi(List<MarkerInfo> markerInfoArrayList);
        void showProgressBar();
        void hideProgressBar();
    }

    interface Presenter {
        void fetchCustomPoiList();
        void showProgressBar();
        void hideProgressBar();
    }
}
