package com.vta.virtualtour.ui.activities.splashScreen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.vta.virtualtour.R;
import com.vta.virtualtour.ui.activities.BaseActivity;
import com.vta.virtualtour.ui.activities.routeChooserScreen.RouteChooserActivity;
import com.vta.virtualtour.ui.activities.routesScreen.RoutesActivity;
import com.vta.virtualtour.ui.activities.tutorialScreen.TutorialActivity;
import com.vta.virtualtour.utility.Constants;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends BaseActivity implements SplashContract.View {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private SplashContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Crashlytics
        Fabric.with(this, new Crashlytics());

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_splash);

        presenter = new SplashPresenter(this);

        // making notification bar transparent
        changeStatusBarColor();

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                presenter.showTutorialOrRouteChooserScreen();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showTutorialScreen() {
        Intent i = new Intent(SplashActivity.this, TutorialActivity.class);
        startActivity(i);
        // close this activity
        finish();
    }

    @Override
    public void showRouteChooserScreen() {
        Intent i = new Intent(SplashActivity.this, RoutesActivity.class);
        startActivity(i);
        // close this activity
        finish();
    }

    //region private methods
    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    //endregion


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constants.PERMISSION_REQUEST_CODE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","Permission granted");
                    showRouteChooserScreen();
                } else {
                    Log.d("TAG","Permission denied");
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
