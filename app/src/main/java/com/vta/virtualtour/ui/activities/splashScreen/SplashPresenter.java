package com.vta.virtualtour.ui.activities.splashScreen;

import com.vta.virtualtour.managers.SharedPreferenceManager;
import com.vta.virtualtour.utility.Constants;

/**
 * Created by tushar
 * Created on 12/04/18.
 */

public class SplashPresenter implements SplashContract.Presenter {

    private SplashContract.View view;

    SplashPresenter(SplashContract.View view) {
        this.view = view;
    }

    @Override
    public void showTutorialOrRouteChooserScreen() {
        if (SharedPreferenceManager.getSharedInstance().getValueForkey(view.getContext(), Constants.SHARED_PREFERENCE_KEY_DO_NOT_SHOW_TUTORIAL_AGAIN)) {
            view.showRouteChooserScreen();
        } else {
            view.showTutorialScreen();
        }
    }
}
