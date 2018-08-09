package com.vta.virtualtour.ui.activities.splashScreen;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.vta.virtualtour.managers.SharedPreferenceManager;
import com.vta.virtualtour.utility.Constants;

/**
 * Created by tushar on 12/04/18.
 */

public class SplashPresenter implements SplashContract.Presenter {

    private SplashContract.View view;

    public SplashPresenter(SplashContract.View view) {
        this.view = view;
    }

    @Override
    public void showTutorialOrRouteChooserScreen() {
        if(SharedPreferenceManager.getSharedInstance(view.getContext()).getValueForkey(Constants.SHARED_PREFERENCE_KEY_DO_NOT_SHOW_TUTORIAL_AGAIN)) {
            view.showRouteChooserScreen();
        } else {
            view.showTutorialScreen();
        }
    }
}
