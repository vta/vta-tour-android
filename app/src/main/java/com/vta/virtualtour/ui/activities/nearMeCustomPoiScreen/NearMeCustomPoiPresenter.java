package com.vta.virtualtour.ui.activities.nearMeCustomPoiScreen;

import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.MarkerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sidhesh.naik on 30/07/18.
 */

public class NearMeCustomPoiPresenter implements NearMeCustomPoiContract.Presenter {

    private NearMeCustomPoiContract.View view;

    public NearMeCustomPoiPresenter(NearMeCustomPoiContract.View view) {
        this.view = view;
    }
    @Override
    public void fetchCustomPoiList() {

        RouteManager.getSharedInstance().fetchCustomPoi(new RouteManager.FetchCustomPoiListener() {
            @Override
            public void didFinishFetchingCustomPois(List<MarkerInfo> customPois, String error) {
                view.showCustomPoi(customPois);
            }
        });

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
