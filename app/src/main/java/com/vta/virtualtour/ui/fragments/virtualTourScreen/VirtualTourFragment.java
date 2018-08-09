package com.vta.virtualtour.ui.fragments.virtualTourScreen;

import android.Manifest;
import android.content.Context;
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
import android.widget.CheckBox;
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
import com.vta.virtualtour.R;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.ui.customView.NoDefaultSpinner;
import com.vta.virtualtour.ui.fragments.BaseFragment;
import com.vta.virtualtour.utility.Constants;
import com.vta.virtualtour.utility.FetchCurrentLocation;
import com.vta.virtualtour.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.vta.virtualtour.utility.Constants.ROUTE_DETAILS_VIDEO_GEOPOINTS_FRONT;
import static com.vta.virtualtour.utility.Constants.ROUTE_DETAILS_VIDEO_GEOPOINTS_LOW_RES_FRONT;

public class VirtualTourFragment extends BaseFragment implements View.OnClickListener, VirtualTourContract.View, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private VirtualTourFragmentInteractionListener mListener;
    private Button viewRouteButton;
    private VirtualTourContract.Presenter presenter;
    private static final String TAG = "VirtualTourFragment";
    private AutoCompleteTextView autoCompleteTextViewRoutes;
    private NoDefaultSpinner spinnerDirection;
    private NoDefaultSpinner spinnerDeparture;
    private NoDefaultSpinner spinnerDestination;

    private LinearLayout progressBarLayout;
    private TextView progressBarTextView;
    private CheckBox currentLocationCheckbox;
    private String selectedRoute;
    private ImageView clearRouteImageView;

    public VirtualTourFragment() {
        // Required empty public constructor
    }

    public static VirtualTourFragment newInstance() {
        VirtualTourFragment fragment = new VirtualTourFragment();
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
        View view = inflater.inflate(R.layout.fragment_virtual_tour, container, false);

        presenter = new VirtualTourPresenter(this);
        setupViews(view);
        initializeSpinnerWithTempList();
        return view;
    }

    //region private methods
    private void setupViews(View view) {
        viewRouteButton = view.findViewById(R.id.routes_button_view_route);
        viewRouteButton.setOnClickListener(this);

        progressBarLayout = view.findViewById(R.id.progress_layout);
        progressBarTextView = view.findViewById(R.id.progress_bar_textview);
        autoCompleteTextViewRoutes = view.findViewById(R.id.routes_auto_complete_text_view);
        autoCompleteTextViewRoutes.setOnClickListener(this);
        autoCompleteTextViewRoutes.setOnItemClickListener(this);
        autoCompleteTextViewRoutes.setEnabled(false);
        spinnerDirection = view.findViewById(R.id.direction_spinner);
        spinnerDirection.setOnItemSelectedListener(this);
        spinnerDirection.setEnabled(false);
        spinnerDeparture = view.findViewById(R.id.departure_spinner);
        spinnerDeparture.setOnItemSelectedListener(this);
        spinnerDeparture.setEnabled(false);
        spinnerDestination = view.findViewById(R.id.destination_spinner);
        spinnerDestination.setOnItemSelectedListener(this);
        spinnerDestination.setEnabled(false);
        currentLocationCheckbox = view.findViewById(R.id.route_chooser_checkbox_current_location);
        currentLocationCheckbox.setOnClickListener(this);
        clearRouteImageView = view.findViewById(R.id.route_chooser_clear_routes);
        clearRouteImageView.setOnClickListener(this);
    }

    private void initializeSpinnerWithTempList() {
        List<String> dummyList = new ArrayList<>();
        dummyList.add("dummyData");

        ArrayAdapter<String> directionAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_dropdpwn, dummyList);
        spinnerDirection.setAdapter(directionAdapter);

        ArrayAdapter<String> destinationAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_dropdpwn, dummyList);
        spinnerDestination.setAdapter(destinationAdapter);

        ArrayAdapter<String> departureAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_dropdpwn, dummyList);
        spinnerDeparture.setAdapter(departureAdapter);

        clearRouteImageView.setVisibility(View.INVISIBLE);
        enableDirectionSpinner(false);
        enableDepartureSpinner(false);
        enableDestinationSpinner(false);
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

    /**
     * shows dialog to turn on location
     */
    private void displayLocationSettingsRequest(Context context) {
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
                        changeCheckBoxState(false);
                        break;
                }
            }
        });
    }

    public void changeCheckBoxState(boolean setChecked) {
        currentLocationCheckbox.setChecked(setChecked);
    }

    public void checkForLocationPermission() {
        presenter.checkForLocationPermission();
    }

    private boolean checkIfRouteSelected() {
        if (RouteManager.getSharedInstance().getRoute() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_route), Toast.LENGTH_SHORT).show();
            return false;
        } else if (RouteManager.getSharedInstance().getRouteDetails() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_direction), Toast.LENGTH_SHORT).show();
            return false;
        } else if (RouteManager.getSharedInstance().getSelectedDeparture() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_departure), Toast.LENGTH_SHORT).show();
            return false;
        } else if (RouteManager.getSharedInstance().getSelectedDestination() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_destination), Toast.LENGTH_SHORT).show();
            return false;
        } else if (RouteManager.getSharedInstance().getSelectedDeparture().getName().equals(RouteManager.getSharedInstance().getSelectedDestination().getName())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_different_departure_destination), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //endregion

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof VirtualTourFragmentInteractionListener) {
            mListener = (VirtualTourFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement VirtualTourFragmentInteractionListener");
        }
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
        super.onResume();
        if (FetchCurrentLocation.getSharedInstance().mGoogleApiClient != null) {
            if (FetchCurrentLocation.getSharedInstance().mGoogleApiClient.isConnected()) {
                FetchCurrentLocation.getSharedInstance().startLocationUpdates();
                Log.d(TAG, "Location update resumed .....................");
            }
        } else {
            presenter.fetchRoutes();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
        super.onDestroy();
        RouteManager.getSharedInstance().clearInstance();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.routes_button_view_route:
                if (mListener != null && checkIfRouteSelected())
                    mListener.onViewRouteClicked(spinnerDirection.getSelectedItem().toString(), selectedRoute);
                break;

            case R.id.routes_auto_complete_text_view:
                makeAutoCompleteTextViewFocusable();
                autoCompleteTextViewRoutes.showDropDown();
                clearRouteImageView.setVisibility(View.VISIBLE);
                break;

            case R.id.route_chooser_checkbox_current_location:
                //check's if location is enabled in phone's setting
                displayLocationSettingsRequest(getActivity());
                break;
            case R.id.route_chooser_clear_routes:
                presenter.clearRouteChooserFields();
                break;
        }
    }

    @Override
    public void reloadRoutes(List<String> routes) {
        ArrayAdapter<String> routesAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_dropdpwn, routes);
        autoCompleteTextViewRoutes.setAdapter(routesAdapter);
        progressBarLayout.setVisibility(View.GONE);
        makeAutoCompleteTextViewFocusable();
    }

    @Override
    public void reloadDirections(List<String> directions) {
        ArrayAdapter<String> directionAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_dropdpwn, directions);
        spinnerDirection.setAdapter(directionAdapter);
    }

    @Override
    public void reloadDepartures(List<String> departures) {
        ArrayAdapter<String> departureAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_dropdpwn, departures);
        spinnerDeparture.setAdapter(departureAdapter);
    }

    @Override
    public void reloadDestinations(List<String> destinations) {
        ArrayAdapter<String> destinationAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_dropdpwn, destinations);
        spinnerDestination.setAdapter(destinationAdapter);
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

    @Override
    public void enableRoutesTextView(boolean shouldEnable) {
        autoCompleteTextViewRoutes.setEnabled(shouldEnable);
    }

    @Override
    public void enableDirectionSpinner(boolean shouldEnable) {
        spinnerDirection.setEnabled(shouldEnable);
    }

    @Override
    public void enableDepartureSpinner(boolean shouldEnable) {
        spinnerDeparture.setEnabled(shouldEnable);
    }

    @Override
    public void enableDestinationSpinner(boolean shouldEnable) {
        spinnerDestination.setEnabled(shouldEnable);
    }

    @Override
    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
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
    public void clearRouteChooserFields() {
        autoCompleteTextViewRoutes.setText("");
        initializeSpinnerWithTempList();
    }

    @Override
    public void locationPermissionGranted() {
        presenter.clearRouteChooserFields();
        if (currentLocationCheckbox.isChecked()) {
            // User current location
            autoCompleteTextViewRoutes.setEnabled(false);
            presenter.fetchCurrentLocation();
            FetchCurrentLocation.getSharedInstance().mGoogleApiClient.connect();
            if (FetchCurrentLocation.getSharedInstance().mGoogleApiClient.isConnected()) {
                FetchCurrentLocation.getSharedInstance().startLocationUpdates();
                Log.d(TAG, "Location update resumed .....................");
            }
            RouteManager.getSharedInstance().setCurrentLocationCheckBoxChecked(true);
        } else {
            FetchCurrentLocation.getSharedInstance().stopLocationUpdates();
            progressBarLayout.setVisibility(View.GONE);
            presenter.fetchRoutes();
            RouteManager.getSharedInstance().setCurrentLocationCheckBoxChecked(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedRoute = ((TextView) view).getText().toString();
        presenter.populateDirections(position);
        presenter.onRouteSelected();
        autoCompleteTextViewRoutes.setSelection(0);
        makeAutoCompleteTextViewNonFocusable();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.direction_spinner) {
            RouteManager.getSharedInstance().setSelectedDirectionPosition(position);
            presenter.fetchRouteDetails(position);
            if (Utils.isConnectedToWifi(getActivity())) {
                presenter.fetchRouteDetailsVideoGeoPoints(ROUTE_DETAILS_VIDEO_GEOPOINTS_FRONT, position);
            } else {
                presenter.fetchRouteDetailsVideoGeoPoints(ROUTE_DETAILS_VIDEO_GEOPOINTS_LOW_RES_FRONT, position);
            }
        } else if (parent.getId() == R.id.departure_spinner) {
            presenter.setDeparture(position);
        } else if (parent.getId() == R.id.destination_spinner) {
            presenter.setDestination(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void currentLatLong(String latitude, String longitude) {
        Log.d(TAG, "Latitude = " + latitude + ", longitude = " + longitude);
        progressBarTextView.setText(getResources().getString(R.string.fetch_routes));
//        presenter.fetchRoutes(latitude, longitude);
    }

    public interface VirtualTourFragmentInteractionListener {
        void onViewRouteClicked(String direction, String routeName);
    }

}
