package com.vta.virtualtour.ui.activities.tutorialScreen;

import android.content.Context;

/**
 * Created by tushar
 * Created on 13/04/18.
 */

public interface TutorialContract {

    interface View {
        Context getContext();

        void showRouteChooserScreen();

        int getViewPagerItem(int position);

        void changeNextButtonText(String text);

        void changeSkipButtonVisibility(boolean isVisible);
    }

    interface Presenter {
        void showRouteChooserScreen();

        int getViewPagerItem(int position);

        void changeNextButtonText(String text);

        void changeSkipButtonVisibility(boolean isVisible);

        void setTutorialDoNotShowAgain();
    }
}
