package com.vta.virtualtour.ui.activities.placeOfInterestDetailsScreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vta.virtualtour.R;
import com.vta.virtualtour.models.PlaceOfInterest;
import com.vta.virtualtour.ui.activities.BaseActivity;

public class PlaceOfInterestDetailsActivity extends BaseActivity implements View.OnClickListener, PlaceOfInterestDetailsContract.View {

    private ImageView placeOfInterestImage;
    private ImageView fab;
    private PlaceOfInterest placeOfInterestDetails;
    private RatingBar ratingBar;
    private TextView placeOfInterestName, placeOfInterestAvailability, placeOfInterestAddress, placeOfInterestPhone, placeOfInterestWebsite,placeOfInterestDistanceTime;
    private LinearLayout progressBarLayout;
    private PlaceOfInterestDetailsContract.Presenter presenter;
    private int distanceAndPoiDetailsApiCalls = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_place_of_interest_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        //set up views
        setUpViews();

        //pass context to presenter
        presenter = new PlaceOfInterestDetailsPresenter(this);

        if (getIntent().getExtras() != null) {
            placeOfInterestDetails = getIntent().getExtras().getParcelable("placeOfInterestDetails");
        }

        if (placeOfInterestDetails != null) {
            presenter.fetchPoiDetails(placeOfInterestDetails.getId(), placeOfInterestDetails.getCurrentLatLong(), placeOfInterestDetails.getPlaceLatLong(), placeOfInterestDetails.getImageUrl());
            presenter.fetchDistanceTimeToPoi(placeOfInterestDetails.getCurrentLatLong(), placeOfInterestDetails.getPlaceLatLong());
        }


    }

    private void populateData(PlaceOfInterest placeOfInterestDetails) {
        if (placeOfInterestDetails.getImageUrl() != null && !placeOfInterestDetails.getImageUrl().isEmpty()) {
            Picasso.with(getApplicationContext()).load(placeOfInterestDetails.getImageUrl()).placeholder(R.drawable.vta_logo).into(placeOfInterestImage);
        }
//        Picasso.with(getApplicationContext()).load(placeOfInterestDetails.getImageUrl()).fit().centerCrop()
//                .placeholder(R.drawable.user_placeholder)
//                .error(R.drawable.user_placeholder_error)
//                .into(placeOfInterestImage);
        placeOfInterestName.setText(placeOfInterestDetails.getName().toUpperCase());

        if (placeOfInterestDetails.getAvailability() != null && !placeOfInterestDetails.getAvailability().isEmpty()) {
            placeOfInterestAvailability.setText(placeOfInterestDetails.getAvailability());
            placeOfInterestAvailability.setVisibility(View.VISIBLE);
        }

        if (placeOfInterestDetails.getAddress() != null && !placeOfInterestDetails.getAddress().isEmpty()) {
            placeOfInterestAddress.setText(placeOfInterestDetails.getAddress());
            placeOfInterestAddress.setVisibility(View.VISIBLE);
        }

        if (placeOfInterestDetails.getPhone() != null && !placeOfInterestDetails.getPhone().isEmpty()) {
            placeOfInterestPhone.setText(placeOfInterestDetails.getPhone());
            placeOfInterestPhone.setVisibility(View.VISIBLE);
        }

        if (placeOfInterestDetails.getWebsite() != null && !placeOfInterestDetails.getWebsite().isEmpty()) {
            placeOfInterestWebsite.setText("View Website");
            placeOfInterestWebsite.setVisibility(View.VISIBLE);
        }

        ratingBar.setRating(placeOfInterestDetails.getRatings());
    }

    private void setUpViews() {
        placeOfInterestName = findViewById(R.id.plcae_of_interest_name);
        placeOfInterestAvailability = findViewById(R.id.availability);
        placeOfInterestAddress = findViewById(R.id.address);
        placeOfInterestPhone = findViewById(R.id.phone_number);
        placeOfInterestWebsite = findViewById(R.id.website);
        progressBarLayout = findViewById(R.id.progress_layout);
        ratingBar = findViewById(R.id.rating_bar);
        fab = findViewById(R.id.poi_detail_button_walk);
        placeOfInterestImage = findViewById(R.id.header_logo);
        placeOfInterestDistanceTime = findViewById(R.id.poi_distance_time);
        placeOfInterestPhone.setClickable(true);
        placeOfInterestWebsite.setClickable(true);
        placeOfInterestPhone.setOnClickListener(this);
        placeOfInterestWebsite.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    private void hideProgressBar() {
        progressBarLayout.setVisibility(View.GONE);
    }

    private void checkIfApiCallsCompleted(){
        if (distanceAndPoiDetailsApiCalls == 2){
            hideProgressBar();
        }
    }


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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.poi_detail_button_walk:
                presenter.onMapNavigationButtonClick();
                break;
            case R.id.phone_number:
                presenter.onPhoneNumberButtonClick();

                break;
            case R.id.website:
                presenter.onWebsiteButtonClick();
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "Permission granted");
                    presenter.callPhoneNumber();
                } else {
                    Log.d("TAG", "Permission denied");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showMapNavigation() {
        String uri = "http://maps.google.com/maps?saddr=" + placeOfInterestDetails.getCurrentLatLong().latitude + "," + placeOfInterestDetails.getCurrentLatLong().longitude + "&daddr=" + placeOfInterestDetails.getPlaceLatLong().latitude + "," + placeOfInterestDetails.getPlaceLatLong().longitude + "&mode=walking";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    @Override
    public void showPhoneNumberDial() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + placeOfInterestDetails.getPhone()));
        startActivity(intent);
    }

    @Override
    public void openWebsite() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(placeOfInterestDetails.getWebsite()));
        startActivity(i);
    }

    @Override
    public void displayPoiDetails(PlaceOfInterest placeOfInterest) {
        placeOfInterestDetails = placeOfInterest;
        placeOfInterestDetails.setPhone(placeOfInterest.getPhone());
        placeOfInterestDetails.setWebsite(placeOfInterest.getWebsite());
        populateData(placeOfInterest);
        distanceAndPoiDetailsApiCalls++;
        checkIfApiCallsCompleted();
    }

    @Override
    public void showProgressBar() {
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDistanceTimeToPoi(String distanceTimeString) {
        placeOfInterestDistanceTime.setText(distanceTimeString);
        distanceAndPoiDetailsApiCalls++;
        checkIfApiCallsCompleted();
    }

}
