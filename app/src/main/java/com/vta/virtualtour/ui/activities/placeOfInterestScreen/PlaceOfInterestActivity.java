package com.vta.virtualtour.ui.activities.placeOfInterestScreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.R;
import com.vta.virtualtour.adapters.CustomSpinnerArrayAdapter;
import com.vta.virtualtour.adapters.PlaceOfInterestListAdapter;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.PlaceOfInterest;
import com.vta.virtualtour.models.PlaceOfInterestCategory;
import com.vta.virtualtour.ui.activities.BaseActivity;
import com.vta.virtualtour.ui.activities.placeOfInterestDetailsScreen.PlaceOfInterestDetailsActivity;
import com.vta.virtualtour.ui.customView.NoDefaultSpinner;
import com.vta.virtualtour.utility.FetchCurrentLocation;

import java.util.ArrayList;
import java.util.List;

public class PlaceOfInterestActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, PlaceOfInterestContract.View, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, FetchCurrentLocation.FetchCurrentLocationListener {
    private static final String TAG = "PlaceOfInterestActivity";
    private NoDefaultSpinner categorySpinner;
    private RecyclerView recyclerView;
    private PlaceOfInterestListAdapter placeOfInterestListAdapter;
    private ArrayList<PlaceOfInterest> placeOfInterestOriginalArrayList = new ArrayList<>();
    private ArrayList<PlaceOfInterest> placeOfInterestArrayList = new ArrayList<>();
    private PlaceOfInterestContract.Presenter presenter;
    private ArrayList<PlaceOfInterestCategory> categories = new ArrayList<>();
    private CustomSpinnerArrayAdapter categoryAdapter;
    private LinearLayout progressBarLayout;
    private TextView progressBarTextView;
    private boolean fetchCurrentLocationOnce = false;

    private GoogleApiClient googleApiClient;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final String LOG_TAG = "PlaceOfInterestActivity";
    private boolean fromNearMe = false;
    private String nearMePoiCategory = "";
    private LinearLayout spinnerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_of_interest);
        setUpViews();
        initializeSpinnerWithTempList();
        setupGoogleApiClient();
        setUpRecyclerListAdapter();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        presenter = new PlaceOfInterestPresenter(this);
        checkForCarCurrentLocation();

    }

    private void checkForCarCurrentLocation() {
        progressBarLayout.setVisibility(View.VISIBLE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean("fromNearMe")){
            //hides poi type dropdown view
            spinnerContainer.setVisibility(View.GONE);
            fromNearMe = true;
            nearMePoiCategory = bundle.getString("category");
            fetchNearbyPoi(bundle.getString("latitude"), bundle.getString("longitude"),fromNearMe);
        } else {
            if (RouteManager.getSharedInstance().isCurrentLocationCheckBoxChecked()) {
                progressBarTextView.setText(getResources().getString(R.string.fetch_current_location));
                presenter.fetchCurrentLocation();
            } else {
                if (bundle != null) {
                    fetchNearbyPoi(bundle.getString("latitude"), bundle.getString("longitude"),fromNearMe);
                }

            }
        }
    }

    private void initializeSpinnerWithTempList() {

        List<String> dummyList = new ArrayList<>();
        dummyList.add("dummyData");

        ArrayAdapter<String> directionAdapter = new ArrayAdapter<String>(this, R.layout.layout_spinner_dropdpwn, dummyList);
        categorySpinner.setAdapter(directionAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        if (RouteManager.getSharedInstance().isCurrentLocationCheckBoxChecked() && FetchCurrentLocation.getSharedInstance() != null && !FetchCurrentLocation.getSharedInstance().mGoogleApiClient.isConnected()) {
            FetchCurrentLocation.getSharedInstance().mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (RouteManager.getSharedInstance().isCurrentLocationCheckBoxChecked() && FetchCurrentLocation.getSharedInstance() != null && FetchCurrentLocation.getSharedInstance().mGoogleApiClient.isConnected()) {
            FetchCurrentLocation.getSharedInstance().startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (RouteManager.getSharedInstance().isCurrentLocationCheckBoxChecked() && FetchCurrentLocation.getSharedInstance() != null && FetchCurrentLocation.getSharedInstance().mGoogleApiClient.isConnected()) {
            FetchCurrentLocation.getSharedInstance().stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        if (RouteManager.getSharedInstance().isCurrentLocationCheckBoxChecked() && FetchCurrentLocation.getSharedInstance() != null && FetchCurrentLocation.getSharedInstance().mGoogleApiClient.isConnected()) {
            FetchCurrentLocation.getSharedInstance().mGoogleApiClient.disconnect();
        }
    }

    //region private methods
    private void setupGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(PlaceOfInterestActivity.this, this, this)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .build();
    }

    private void setUpRecyclerListAdapter() {
        placeOfInterestListAdapter = new PlaceOfInterestListAdapter(getApplicationContext(), placeOfInterestArrayList, new PlaceOfInterestListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PlaceOfInterest placeOfInterest) {
                Intent intent = new Intent(PlaceOfInterestActivity.this, PlaceOfInterestDetailsActivity.class);
                intent.putExtra("placeOfInterestDetails", placeOfInterest);
                startActivity(intent);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(placeOfInterestListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private void setUpSpinner() {

        // Creating adapter for spinner
        categoryAdapter = new CustomSpinnerArrayAdapter(this, categories);

        // Drop down layout style - list view with radio button
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
//        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.layout_spinner_dropdpwn, categories);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(this);

    }

    private void setUpViews() {
        // Spinner element
        categorySpinner = (NoDefaultSpinner) findViewById(R.id.spinner);

        // Spinner click listener
        categorySpinner.setOnItemSelectedListener(this);

        recyclerView = findViewById(R.id.recycler_view);

        progressBarLayout = findViewById(R.id.progress_layout);
        progressBarTextView = findViewById(R.id.progress_bar_textview);
        spinnerContainer = findViewById(R.id.spinner_container);
    }

    private void filterPoiList(ArrayList<PlaceOfInterest> placeOfInterests){
        placeOfInterestArrayList.clear();
        placeOfInterestArrayList.addAll(placeOfInterests);
        placeOfInterestListAdapter.notifyDataSetChanged();
    }

    private void fetchNearbyPoi(String latitude,String longitude,boolean fromNearMe){
        progressBarTextView.setText(getResources().getString(R.string.loading_poi));
        presenter.fetchPlaceOfInterestList(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)),fromNearMe);
        if (RouteManager.getSharedInstance().isCurrentLocationCheckBoxChecked() && FetchCurrentLocation.getSharedInstance() != null){
            FetchCurrentLocation.getSharedInstance().stopLocationUpdates();
        }
    }

    //endregion

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        placeOfInterestArrayList.clear();
        placeOfInterestArrayList.addAll(placeOfInterestOriginalArrayList);

        if (!categories.get(position).getKey().equals("all")) {
            ArrayList<PlaceOfInterest> filteredArrayList = new ArrayList<>();
            for (int i = 0; i < placeOfInterestArrayList.size(); i++) {
                for (int j = 0; j < placeOfInterestArrayList.get(i).getTypes().size(); j++) {
                    if (categories.get(position).getKey().equals(placeOfInterestArrayList.get(i).getTypes().get(j))) {
                        filteredArrayList.add(placeOfInterestArrayList.get(i));
                        break;
                    }
                }
            }
            filterPoiList(filteredArrayList);
        }else {
            filterPoiList(placeOfInterestOriginalArrayList);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setupLocationManager() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //call this to set the activity context for the first time
        FetchCurrentLocation.getSharedInstance().setUpLocationListener(this);
    }

    @Override
    public void showCategory(ArrayList<PlaceOfInterestCategory> categories) {
        this.categories.clear();
        this.categories.add(new PlaceOfInterestCategory("All","all"));
        for (int i = 0; i < categories.size(); i++){
            this.categories.add(new PlaceOfInterestCategory(categories.get(i).getName(),categories.get(i).getKey()));
        }
        setUpSpinner();
    }

    @Override
    public void reloadPlaceOfInterestList(ArrayList<PlaceOfInterest> placeOfInterestList) {
        placeOfInterestArrayList.clear();
        placeOfInterestOriginalArrayList.clear();
        placeOfInterestArrayList.addAll(placeOfInterestList);
        placeOfInterestOriginalArrayList.addAll(placeOfInterestList);
        placeOfInterestListAdapter.notifyDataSetChanged();
        if (progressBarLayout.getVisibility() == View.VISIBLE) {
            progressBarLayout.setVisibility(View.GONE);
        }

        if (fromNearMe){
            filterPoiUsingCategory();
        }
    }

    private void filterPoiUsingCategory() {
        ArrayList<PlaceOfInterest> filteredArrayList = new ArrayList<>();
        for (int i = 0; i < placeOfInterestArrayList.size(); i++) {
            for (int j = 0; j < placeOfInterestArrayList.get(i).getTypes().size(); j++) {
                if (nearMePoiCategory.equals(placeOfInterestArrayList.get(i).getTypes().get(j))) {
                    filteredArrayList.add(placeOfInterestArrayList.get(i));
                    break;
                }
            }
        }
        filterPoiList(filteredArrayList);
    }

    @Override
    public void currentLatLong(String latitude, String longitude) {
        Log.d(TAG, "Latitude = " + latitude + ", longitude = " + longitude);
//        presenter.fetchCategory(null);
        if (!fetchCurrentLocationOnce) {
            fetchNearbyPoi(latitude,longitude,fromNearMe);
        }

        fetchCurrentLocationOnce = true;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
