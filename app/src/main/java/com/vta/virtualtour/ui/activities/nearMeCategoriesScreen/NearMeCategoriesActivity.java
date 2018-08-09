package com.vta.virtualtour.ui.activities.nearMeCategoriesScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.R;
import com.vta.virtualtour.adapters.NearMeCategoriesAdapter;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.NearByCategory;
import com.vta.virtualtour.models.Route;
import com.vta.virtualtour.models.Stop;
import com.vta.virtualtour.ui.activities.BaseActivity;
import com.vta.virtualtour.ui.activities.nearMeCategoryDetailsScreen.NearMeCategoryDetailsActivity;
import com.vta.virtualtour.ui.activities.nearMeCustomPoiScreen.NearMeCustomPoiActivity;
import com.vta.virtualtour.ui.activities.placeOfInterestScreen.PlaceOfInterestActivity;

import java.util.ArrayList;

/**
 * Created by tushar on 7/19/2018.
 */
public class NearMeCategoriesActivity extends BaseActivity implements NearMeCategoriesContract.View {

    private NearMeCategoriesContract.Presenter presenter;
    private RecyclerView recyclerView;
    private LinearLayout progressBarLayout;
    private TextView progressBarTextView;
    private NearMeCategoriesAdapter nearMeCategoriesAdapter;
    private ArrayList<NearByCategory> nearByCategories = new ArrayList<>();
    private Stop stop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me_categories);

        if (RouteManager.getSharedInstance().getNearMeStopList().size() != 0)
            stop = RouteManager.getSharedInstance().getNearMeStopList().get(0);
        presenter = new NearMeCategoriesPresenter(this);
        setupViews();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_title_layout);
        presenter.fetchIntegrations();
        fetchNearbyCategories();
    }

    private void fetchNearbyCategories() {
        if (stop != null){
            presenter.showProgressBar();
            progressBarTextView.setText(getResources().getString(R.string.loading_nearby_type));
            presenter.fetchPlaceOfInterestList(new LatLng(stop.getLat(), stop.getLng()));
            ((TextView) findViewById(R.id.action_bar_title)).setText(stop.getName());
        }
    }

    private void setUpRecyclerListAdapter() {
        nearMeCategoriesAdapter = new NearMeCategoriesAdapter(this, nearByCategories, new NearMeCategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NearByCategory nearByCategory) {
                if (nearByCategory.isPoiCategory()){
                    Intent intent = new Intent(NearMeCategoriesActivity.this, PlaceOfInterestActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("latitude", stop.getLat()+"");
                    bundle.putString("longitude", stop.getLng()+"");
                    bundle.putBoolean("fromNearMe", true);
                    bundle.putString("category", nearByCategory.getKey());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (nearByCategory.getKey().equals("custom_poi")){
                    Intent intent = new Intent(NearMeCategoriesActivity.this, NearMeCustomPoiActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(NearMeCategoriesActivity.this, NearMeCategoryDetailsActivity.class);
                    intent.putExtra("stop", stop);
                    intent.putExtra("nearByCategory", nearByCategory);
                    startActivity(intent);
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(nearMeCategoriesAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recycler_view);
        progressBarLayout = findViewById(R.id.progress_layout);
        progressBarTextView = findViewById(R.id.progress_bar_textview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for (int i = 0; i < RouteManager.getSharedInstance().getNearMeStopList().size(); i++){
            menu.add(Menu.NONE, i, Menu.NONE, RouteManager.getSharedInstance().getNearMeStopList().get(i).getName());
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case 0:
                stop = RouteManager.getSharedInstance().getNearMeStopList().get(0);
                fetchNearbyCategories();
                return true;
            case 1:
                stop = RouteManager.getSharedInstance().getNearMeStopList().get(1);
                fetchNearbyCategories();
                return true;
            case 2:
                stop = RouteManager.getSharedInstance().getNearMeStopList().get(2);
                fetchNearbyCategories();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showCategory(ArrayList<NearByCategory> categories) {
        nearByCategories = new ArrayList<>();
        nearByCategories.addAll(categories);
        setUpRecyclerListAdapter();
        presenter.hideProgressBar();
    }

    @Override
    public void showProgressBar() {
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBarLayout.setVisibility(View.GONE);
    }
}
