package com.vta.virtualtour;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.maps.MapsInitializer;

/**
 * Created by tushar
 * Created on 23/05/18.
 */

public class VirtualTourApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
        MapsInitializer.initialize(this);
    }

    public static Context getAppContext() {
        return appContext;
    }
}
