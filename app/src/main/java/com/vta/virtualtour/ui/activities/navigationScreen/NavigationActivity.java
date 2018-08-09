package com.vta.virtualtour.ui.activities.navigationScreen;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.vta.virtualtour.R;
import com.vta.virtualtour.adapters.InfoWindowCustom;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.ui.activities.BaseActivity;
import com.vta.virtualtour.utility.Utils;

import java.util.List;

public class NavigationActivity extends BaseActivity implements NavigationContract.View, OnMapReadyCallback, View.OnClickListener {

    private static final int overview = 0;
    private MapFragment mapFragment;
    private GoogleMap map;
    private TextView stepsTextView;
    private NavigationContract.Presenter presenter;
    private ImageView mapFullscreenButton;
    private ScrollView directionsLayout;
    private boolean isFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_navigation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
//        getSupportActionBar().setTitle(getIntent().getStringExtra("Direction") + "/" + getIntent().getStringExtra("RouteName"));
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_title_layout);
        ((TextView) findViewById(R.id.action_bar_title)).setText(
                getIntent().getStringExtra("Direction") + "/" + getIntent().getStringExtra("RouteName"));
        presenter = new NavigationPresenter(this);

        setupViews();
        allSetup();
    }

    private void setupViews() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.navigation_map);
        stepsTextView = findViewById(R.id.navigation_text_details);
        mapFullscreenButton = findViewById(R.id.navigation_imageview_fullscreen);
        mapFullscreenButton.setOnClickListener(this);
        directionsLayout = findViewById(R.id.navigation_layout_directions);
    }

    private void allSetup() {
        initMap();

        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(new SensorEventListener() {
            int orientation = -1;

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[1] < 6.5 && event.values[1] > -6.5) {
                    if (orientation != 1) {
                        if (isFullscreen) {
                            Log.d("Sensor", "Landscape fullscreen");
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        } else {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                        Log.d("Sensor", "Landscape");
                    }
                    orientation = 1;
                } else {
                    if (orientation != 0) {
                        Log.d("Sensor", "Portrait");
                    }
                    orientation = 0;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        }, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    private void initMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(false);

        map.setInfoWindowAdapter(new InfoWindowCustom(NavigationActivity.this));
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(RouteManager.getSharedInstance().getSelectedDeparture().getLat(),
                        RouteManager.getSharedInstance().getSelectedDeparture().getLng());

                com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(RouteManager.getSharedInstance().getSelectedDestination().getLat(),
                        RouteManager.getSharedInstance().getSelectedDestination().getLng());

                presenter.getDirectionDetails(TravelMode.DRIVING);
            }
        });
    }

    @Override
    public void updateMapView(DirectionsResult result) {
        //add polyline
        List<LatLng> decodedPath = PolyUtil.decode(result.routes[overview].overviewPolyline.getEncodedPath());
        map.addPolyline(new PolylineOptions().addAll(decodedPath)
                .width(5)
                .color(getResources().getColor(R.color.colorPrimary)));

        //Zoom to Points
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng item : PolyUtil.decode(result.routes[overview].overviewPolyline.getEncodedPath())) {
            builder.include(item);
        }
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));


        //add markers
        map.addMarker(new MarkerOptions().position(new LatLng(result.routes[overview].legs[overview].startLocation.lat,
                result.routes[overview].legs[overview].startLocation.lng)).title(RouteManager.getSharedInstance().getSelectedDeparture().getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_stop)));
        map.addMarker(new MarkerOptions().position(new LatLng(result.routes[overview].legs[overview].endLocation.lat,
                result.routes[overview].legs[overview].endLocation.lng)).title(RouteManager.getSharedInstance().getSelectedDestination().getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_stop)));
    }

    @Override
    public void setDirections(String directions) {
        //display directions
        stepsTextView.setText(Utils.fromHtml(directions));
    }

    @Override
    public void showFullscreenMapView() {
        if (isFullscreen) {
            getSupportActionBar().hide();
            directionsLayout.setVisibility(View.GONE);
        } else {
            resetViewsToOriginalState();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_imageview_fullscreen:
                isFullscreen = !isFullscreen;
                presenter.fullscreenButtonClicked();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            resetViewsToOriginalState();
            isFullscreen = !isFullscreen;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            finish();
        }
    }

    private void resetViewsToOriginalState() {
        getSupportActionBar().show();
        directionsLayout.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
