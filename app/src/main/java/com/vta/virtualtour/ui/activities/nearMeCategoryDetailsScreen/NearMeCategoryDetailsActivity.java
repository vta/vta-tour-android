package com.vta.virtualtour.ui.activities.nearMeCategoryDetailsScreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vta.virtualtour.R;
import com.vta.virtualtour.adapters.NearMeCategoryDetailsAdapter;
import com.vta.virtualtour.adapters.NearMeMeetupAdapter;
import com.vta.virtualtour.models.MarkerInfo;
import com.vta.virtualtour.models.NearByCategory;
import com.vta.virtualtour.models.Stop;

import java.util.ArrayList;
import java.util.List;

public class NearMeCategoryDetailsActivity extends AppCompatActivity implements NearMeCategoryDetailsContract.View {

    private final String connectionsBaseURL = "http://www.vta.org/routes/rt";

    private NearMeCategoryDetailsContract.Presenter presenter;
    private RecyclerView recyclerView;
    private LinearLayout progressBarLayout;
    private NearMeCategoryDetailsAdapter nearMeCategoryDetailsAdapter;
    private NearMeMeetupAdapter nearMeMeetupAdapter;

    private TextView progressBarTextView;
    private NearByCategory nearByCategory;
    private Stop stop;
    private List<String> nearByPlaceOfInterests = new ArrayList<>();
    private List<MarkerInfo> nearByMeetups = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me_category_details);
        stop = getIntent().getExtras().getParcelable("stop");
        nearByCategory = getIntent().getExtras().getParcelable("nearByCategory");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setTitle(stop.getName());
        presenter = new NearMeCategoryDetailsPresenter(this);
        setupViews();
        fetchNearbyPlacesOfInterest();
    }

    private void fetchNearbyPlacesOfInterest() {
        showProgressBar();
        progressBarTextView.setText(getResources().getString(R.string.loading_nearby_type));
        if (nearByCategory.isPoiCategory()) {
            presenter.fetchPlaceOfInterestList(nearByCategory);
        } else {
            if (nearByCategory.getKey().equals("amenity")) {
                presenter.fetchAmenitiesForStop(stop);
            } else if (nearByCategory.getKey().equals("connections")) {
                presenter.fetchConnectionsForStop(stop);
            } else {
                presenter.fetchSocialGatheringForStop(stop);
            }
        }
    }

    private void setUpRecyclerListAdapter() {

        nearMeCategoryDetailsAdapter = new NearMeCategoryDetailsAdapter(nearByCategory, nearByPlaceOfInterests, new NearMeCategoryDetailsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(connectionsBaseURL + name));
                startActivity(i);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(nearMeCategoryDetailsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private void setUpMeetupRecyclerListAdapter() {
        nearMeMeetupAdapter = new NearMeMeetupAdapter(nearByCategory, nearByMeetups, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(nearMeMeetupAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recycler_view);
        progressBarLayout = findViewById(R.id.progress_layout);
        progressBarTextView = findViewById(R.id.progress_bar_textview);
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
    public void reloadRecyclerView(List<String> list) {
        nearByPlaceOfInterests.clear();
        nearByPlaceOfInterests.addAll(list);
        setUpRecyclerListAdapter();
        hideProgressBar();
    }

    @Override
    public void reloadMeetupRecyclerView(List<MarkerInfo> meeetups) {
        nearByMeetups.clear();
        nearByMeetups.addAll(meeetups);
        setUpMeetupRecyclerListAdapter();
        hideProgressBar();
    }

    public void showProgressBar() {
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBarLayout.setVisibility(View.GONE);
    }
}
