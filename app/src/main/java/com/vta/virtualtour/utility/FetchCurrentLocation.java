package com.vta.virtualtour.utility;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;


/**
 * Created by tushar on 23/04/18.
 */

public class FetchCurrentLocation implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private FetchCurrentLocationListener fetchCurrentLocationListener;
    private Activity activity;
    private LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    public GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private static final String TAG = "FetchCurrentLocation";
    private static FetchCurrentLocation fetchCurrentLocation;

    public static FetchCurrentLocation getSharedInstance() {
        if (fetchCurrentLocation == null) {
            fetchCurrentLocation = new FetchCurrentLocation();
        }
        return fetchCurrentLocation;
    }

    public FetchCurrentLocation() {

    }

    public void setUpLocationListener(Activity activity){
        this.activity = activity;
        try {
            fetchCurrentLocationListener = (FetchCurrentLocationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement fetchCurrentLocationProgress");
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        Log.d(TAG, "Location update started ..............: ");
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
            Log.d(TAG, "Location update stopped .......................");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            fetchCurrentLocationListener.currentLatLong(String.valueOf(mCurrentLocation.getLatitude()),String.valueOf(mCurrentLocation.getLongitude()));
        } else {
            Log.d(TAG, "location is null ...............");
            fetchCurrentLocationListener.currentLatLong("","");

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    public interface FetchCurrentLocationListener {
        void currentLatLong(String latitude, String longitude);
    }
}
