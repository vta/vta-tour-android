package com.vta.virtualtour.ui.fragments.nearMeScreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.R;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.Route;
import com.vta.virtualtour.ui.activities.nearMeCategoriesScreen.NearMeCategoriesActivity;
import com.vta.virtualtour.ui.customView.NoDefaultSpinner;
import com.vta.virtualtour.ui.fragments.BaseFragment;
import com.vta.virtualtour.utility.Constants;
import com.vta.virtualtour.utility.FetchCurrentLocation;

import java.util.ArrayList;
import java.util.List;

public class NearMeFragment extends BaseFragment implements View.OnClickListener, NearMeContract.View, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "NearMeFragment";
    private Button viewNearMeButton;
    private NearMeContract.Presenter presenter;
    private AutoCompleteTextView autoCompleteTextViewRoutes;
    private NoDefaultSpinner spinnerDirection;
    private ImageView clearRouteImageView;
    private LinearLayout progressBarLayout;
    private TextView progressBarTextView;
    private LatLng currentLocation;
    private List<Route> routes = new ArrayList<>();

    public NearMeFragment() {
        // Required empty public constructor
    }

    public static NearMeFragment newInstance() {
        NearMeFragment fragment = new NearMeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_near_me, container, false);

        presenter = new NearMePresenter(this);
        setupViews(view);
        initializeSpinnerWithTempList();
        return view;
    }

    //region private methods
    private void setupViews(View view) {
        viewNearMeButton = view.findViewById(R.id.routes_button_view_near_me);
        viewNearMeButton.setOnClickListener(this);
        progressBarLayout = view.findViewById(R.id.progress_layout);
        progressBarTextView = view.findViewById(R.id.progress_bar_textview);
        autoCompleteTextViewRoutes = view.findViewById(R.id.routes_auto_complete_text_view);
        autoCompleteTextViewRoutes.setOnClickListener(this);
        autoCompleteTextViewRoutes.setOnItemClickListener(this);
        autoCompleteTextViewRoutes.setEnabled(false);
        spinnerDirection = view.findViewById(R.id.direction_spinner);
        spinnerDirection.setOnItemSelectedListener(this);
        spinnerDirection.setEnabled(false);
        clearRouteImageView = view.findViewById(R.id.routes_clear_routes);
        clearRouteImageView.setOnClickListener(this);
    }

    private void initializeSpinnerWithTempList() {
        List<String> dummyList = new ArrayList<>();
        dummyList.add("dummyData");

        ArrayAdapter<String> directionAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_dropdpwn, dummyList);
        spinnerDirection.setAdapter(directionAdapter);

        clearRouteImageView.setVisibility(View.INVISIBLE);
        enableDirectionSpinner(false);
    }

    private void makeAutoCompleteTextViewFocusable() {
        autoCompleteTextViewRoutes.setFocusable(true);
        autoCompleteTextViewRoutes.setFocusableInTouchMode(true);
        autoCompleteTextViewRoutes.requestFocus();
    }

    private void makeAutoCompleteTextViewNonFocusable() {
        autoCompleteTextViewRoutes.setFocusable(false);
        autoCompleteTextViewRoutes.setFocusableInTouchMode(false);
        autoCompleteTextViewRoutes.clearFocus();
    }

    private boolean checkIfRouteSelected() {
        if (autoCompleteTextViewRoutes.getText().toString().equals("")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_route), Toast.LENGTH_SHORT).show();
            return false;
        } else if (RouteManager.getSharedInstance().getNearMeStopList() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_direction), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //endregion

    //region public methods
    public void currentLatLong(String latitude, String longitude) {
        Log.d(TAG, "Latitude = " + latitude + ", longitude = " + longitude);
//        FetchCurrentLocation.getSharedInstance().stopLocationUpdates();

        progressBarTextView.setText(getResources().getString(R.string.fetch_routes));
        currentLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        presenter.fetchRoutes(latitude, longitude);
    }

    /**
     * shows dialog to turn on location
     */
    public void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("TAG", "All location settings are satisfied.");
                        checkForLocationPermission();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), Constants.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    public void checkForLocationPermission() {
        presenter.checkForLocationPermission();
    }

    //endregion

    //region fragment lifecycle methods
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        if (FetchCurrentLocation.getSharedInstance().mGoogleApiClient != null) {
            FetchCurrentLocation.getSharedInstance().mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume fired ..............");
        super.onResume();
        if (FetchCurrentLocation.getSharedInstance().mGoogleApiClient != null) {
            if (FetchCurrentLocation.getSharedInstance().mGoogleApiClient.isConnected()) {
                FetchCurrentLocation.getSharedInstance().startLocationUpdates();
                Log.d(TAG, "Location update resumed .....................");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause fired ..............");
        if (FetchCurrentLocation.getSharedInstance().mGoogleApiClient != null) {
            FetchCurrentLocation.getSharedInstance().stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        if (FetchCurrentLocation.getSharedInstance().mGoogleApiClient != null) {
            FetchCurrentLocation.getSharedInstance().mGoogleApiClient.disconnect();
            Log.d(TAG, "isConnected ...............: " + FetchCurrentLocation.getSharedInstance().mGoogleApiClient.isConnected());
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroyed fired ..............");
        super.onDestroy();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //endregion

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.routes_button_view_near_me:
                //// TODO: 20/07/18 Navigate to nearby list screen
                if (checkIfRouteSelected()) {
                    Intent intent = new Intent(getActivity(), NearMeCategoriesActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.routes_auto_complete_text_view:
                makeAutoCompleteTextViewFocusable();
                autoCompleteTextViewRoutes.showDropDown();
                clearRouteImageView.setVisibility(View.VISIBLE);
                break;

            case R.id.routes_clear_routes:
                presenter.clearRouteChooserFields();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int actualPosition = this.routes.indexOf(parent.getItemAtPosition(position));
        presenter.populateDirections(actualPosition);
        presenter.populateDirections(position);
        presenter.onRouteSelected();
        autoCompleteTextViewRoutes.setSelection(0);
        makeAutoCompleteTextViewNonFocusable();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.direction_spinner) {
            presenter.fetchRouteDetails(position, currentLocation);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void showProgressBarLayout() {
        progressBarTextView.setText(getResources().getString(R.string.fetch_routes));
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBarLayout() {
        progressBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void enableRoutesTextView(boolean shouldEnable) {
        autoCompleteTextViewRoutes.setEnabled(shouldEnable);
    }

    @Override
    public void reloadRoutes(List<Route> routes) {
        this.routes = routes;
        ArrayAdapter<Route> routesAdapter = new ArrayAdapter<Route>(getActivity(), R.layout.layout_spinner_dropdpwn, routes);
        autoCompleteTextViewRoutes.setAdapter(routesAdapter);
        hideProgressBarLayout();
        makeAutoCompleteTextViewFocusable();
    }

    @Override
    public void enableDirectionSpinner(boolean shouldEnable) {
        spinnerDirection.setEnabled(shouldEnable);
    }

    @Override
    public void reloadDirections(List<String> directions) {
        ArrayAdapter<String> directionAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_dropdpwn, directions);
        spinnerDirection.setAdapter(directionAdapter);
    }

    @Override
    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    @Override
    public void clearRouteChooserFields() {
        autoCompleteTextViewRoutes.setText("");
        initializeSpinnerWithTempList();
        RouteManager.getSharedInstance().setNearMeStopList(null);
    }

    @Override
    public void locationPermissionGranted() {
        presenter.clearRouteChooserFields();
        // User current location
        autoCompleteTextViewRoutes.setEnabled(false);
        presenter.fetchCurrentLocation();
        FetchCurrentLocation.getSharedInstance().mGoogleApiClient.connect();
        if (FetchCurrentLocation.getSharedInstance().mGoogleApiClient.isConnected()) {
            FetchCurrentLocation.getSharedInstance().startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }

    }

    @Override
    public void setupLocationManager() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        progressBarLayout.setVisibility(View.VISIBLE);
        //call this to set the activity context for the first time
        FetchCurrentLocation.getSharedInstance().setUpLocationListener(getActivity());
    }

}
