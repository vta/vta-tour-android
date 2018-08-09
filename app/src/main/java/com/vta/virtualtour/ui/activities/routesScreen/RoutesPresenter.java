package com.vta.virtualtour.ui.activities.routesScreen;

/**
 * Created by tushar on 7/18/2018.
 */
public class RoutesPresenter implements RoutesContract.Presenter {

    private RoutesContract.View view;
    //TODO: Make it 10 later
    private static final int ROUT_CHOOSER_DISTANCE = 1000;

    public RoutesPresenter(RoutesContract.View view) {
        this.view = view;
    }

    @Override
    public void checkForLocationPermission() {

    }
}
