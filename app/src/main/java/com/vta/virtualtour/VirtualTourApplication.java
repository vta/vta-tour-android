package com.vta.virtualtour;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.gms.maps.MapsInitializer;

/**
 * Created by tushar
 * Created on 23/05/18.
 */

public class VirtualTourApplication extends Application {

    private static Context appContext;

    private HttpProxyCacheServer proxy;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
        MapsInitializer.initialize(this);
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        VirtualTourApplication app = (VirtualTourApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }
}
