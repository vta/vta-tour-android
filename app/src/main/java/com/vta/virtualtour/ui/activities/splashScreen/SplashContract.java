package com.vta.virtualtour.ui.activities.splashScreen;

import android.content.Context;

/**
 * Created by tushar on 12/04/18.
 */

public interface SplashContract {

    interface View {
        Context getContext();
        void showTutorialScreen();
        void showRouteChooserScreen();
    }

    interface Presenter {
        void showTutorialOrRouteChooserScreen();
    }
}
