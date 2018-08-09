package com.vta.virtualtour.ui.activities.routesScreen;

/**
 * Created by tushar on 7/18/2018.
 */
public interface RoutesContract {
    interface View {
    }

    interface Presenter {
        void checkForLocationPermission();
    }
}
