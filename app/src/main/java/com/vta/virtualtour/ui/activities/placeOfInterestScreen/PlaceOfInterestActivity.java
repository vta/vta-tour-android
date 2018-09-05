package com.vta.virtualtour.ui.activities.placeOfInterestScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.R;
import com.vta.virtualtour.adapters.CustomSpinnerArrayAdapter;
import com.vta.virtualtour.adapters.PlaceOfInterestListAdapter;
import com.vta.virtualtour.models.PlaceOfInterest;
import com.vta.virtualtour.models.PlaceOfInterestCategory;
import com.vta.virtualtour.ui.activities.BaseActivity;
import com.vta.virtualtour.ui.activities.placeOfInterestDetailsScreen.PlaceOfInterestDetailsActivity;
import com.vta.virtualtour.ui.customView.NoDefaultSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceOfInterestActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, PlaceOfInterestContract.View, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String[] arrPOI_Type = {"Accounting", "Airport", "Amusement Park", "Aquarium", "Art Gallery", "Atm", "Bakery", "Bank", "Bar", "Beauty Salon", "Bicycle Store", "Book Store", "Bowling Alley", "Bus Station", "Cafe", "Campground", "Car Dealer", "Car Rental", "Car Repair", "Car Wash", "Casino", "Cemetery", "Church", "City Hall", "Clothing Store", "Convenience Store", "Courthouse", "Dentist", "Department Store", "Doctor", "Electrician", "Electronics Store", "Embassy", "Fire Station", "Florist", "Funeral Home", "Furniture Store", "Gas Station", "Gym", "Hair Care", "Hardware Store", "Hindu Temple", "Home Goods Store", "Hospital", "Insurance Agency", "Jewelry Store", "Laundry", "Lawyer", "Library", "Liquor Store", "Local Government Office", "Locksmith", "Lodging", "Meal Delivery", "Meal Takeaway", "Mosque", "Movie Rental", "Movie Theater", "Moving Company", "Museum", "Night Club", "Painter", "Park", "Parking", "Pet Store", "Pharmacy", "Physiotherapist", "Plumber", "Police", "Post Office", "Real Estate Agency", "Restaurant", "Roofing Contractor", "Rv Park", "School", "Shoe Store", "Shopping Mall", "Spa", "Stadium", "Storage", "Store", "Subway Station", "Supermarket", "Synagogue", "Taxi Stand", "Train Station", "Transit Station", "Travel Agency", "Veterinary Care", "Zoo"};

    private PlaceOfInterestContract.Presenter presenter;

    private NoDefaultSpinner categorySpinner;
    private RecyclerView recyclerView;
    private PlaceOfInterestListAdapter placeOfInterestListAdapter;

    private ArrayList<PlaceOfInterest> placeOfInterestArrayList = new ArrayList<>();
    private ArrayList<PlaceOfInterestCategory> categories = new ArrayList<>();

    private LinearLayout progressBarLayout;
    private TextView progressBarTextView;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private LinearLayout spinnerContainer;
    private String selectedPOIType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_of_interest);

        setUpViews();
        initializeCategorySpinner();
        setupGoogleApiClient();
        setUpRecyclerListAdapter();

        selectedPOIType = "bank";

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        // If Opened from Near By - Begin
        String stopName = getIntent().getStringExtra("StopName");
        if (stopName != null) {
            getSupportActionBar().setTitle(stopName);
        }
        // If Opened from Near By - End

        presenter = new PlaceOfInterestPresenter(this);
        initPOIs();
    }

    //region SetUP Methods

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

    private void initializeCategorySpinner() {

        List<String> poiTypes = Arrays.asList(arrPOI_Type);

        this.categories.clear();
        for (int i = 0; i < poiTypes.size(); i++) {
            this.categories.add(new PlaceOfInterestCategory(poiTypes.get(i), poiTypes.get(i)));
        }
        setUpSpinner();
    }

    private void setUpSpinner() {

        CustomSpinnerArrayAdapter categoryAdapter = new CustomSpinnerArrayAdapter(this, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(this);

    }

    private void setupGoogleApiClient() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(PlaceOfInterestActivity.this, this, this)
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

    //endregion

    // region POI Methods
    private void initPOIs() {
        placeOfInterestArrayList.clear();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean("fromNearMe")) {
            // Hides poi type dropdown view in Case of Near Me
            spinnerContainer.setVisibility(View.GONE);
            selectedPOIType = bundle.getString("category");
            fetchPOIs(bundle.getString("latitude"), bundle.getString("longitude"));

        } else {
            if (bundle != null) {
//                fetchPOIs(bundle.getString("latitude"), bundle.getString("longitude"));
            }
        }
    }

    private void fetchPOIs(String latitude, String longitude) {

        progressBarLayout.setVisibility(View.VISIBLE);

        // init default selection as bank and update as category
        selectedPOIType = selectedPOIType.length() == 0 ? "bank" : selectedPOIType;
        selectedPOIType = selectedPOIType.replace(" ", "_").toLowerCase();

        progressBarTextView.setText(getResources().getString(R.string.loading_poi));
        presenter.fetchPlaceOfInterestList(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), selectedPOIType, null);

    }

    //endregion

    // region Overridden Methods

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

        placeOfInterestArrayList.clear();
        selectedPOIType = categories.get(position).getKey();
        Bundle bundle = getIntent().getExtras();
        fetchPOIs(bundle.getString("latitude"), bundle.getString("longitude"));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // endregion

    //region Contract Methods

    @Override
    public Context getContext() {
        return this;
    }


    @Override
    public void reloadPlaceOfInterestList(ArrayList<PlaceOfInterest> placeOfInterestList, String nextPageToken) {
        placeOfInterestArrayList.addAll(placeOfInterestList);
        placeOfInterestListAdapter.notifyDataSetChanged();
        if (progressBarLayout.getVisibility() == View.VISIBLE) {
            progressBarLayout.setVisibility(View.GONE);
        }

        if (nextPageToken != null) {
            Bundle bundle = getIntent().getExtras();
            LatLng location = new LatLng(Double.parseDouble(bundle.getString("latitude")), Double.parseDouble(bundle.getString("longitude")));
            presenter.fetchPlaceOfInterestList(location, selectedPOIType, "");
        }

    }

    //endregion

    // region Google Listener

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // endregion
}
