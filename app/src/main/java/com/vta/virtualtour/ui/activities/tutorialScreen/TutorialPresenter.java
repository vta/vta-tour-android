package com.vta.virtualtour.ui.activities.tutorialScreen;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.vta.virtualtour.managers.SharedPreferenceManager;
import com.vta.virtualtour.utility.Constants;

/**
 * Created by tushar on 13/04/18.
 */

public class TutorialPresenter implements TutorialContract.Presenter {

    private TutorialContract.View view;

    public TutorialPresenter(TutorialContract.View view) {
        this.view = view;
    }

    @Override
    public void showRouteChooserScreen() {
        view.showRouteChooserScreen();
    }

    @Override
    public int getViewPagerItem(int position) {
        return view.getViewPagerItem(position);
    }

    @Override
    public void changeNextButtonText(String text) {
        view.changeNextButtonText(text);
    }

    @Override
    public void changeSkipButtonVisibility(boolean isVisible) {
        view.changeSkipButtonVisibility(isVisible);
    }

    @Override
    public void setTutorialDoNotShowAgain() {
        SharedPreferenceManager.getSharedInstance(view.getContext()).saveValueForKey(Constants.SHARED_PREFERENCE_KEY_DO_NOT_SHOW_TUTORIAL_AGAIN, true);
    }
}
