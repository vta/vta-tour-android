package com.vta.virtualtour.ui.activities.nearMeCustomPoiScreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vta.virtualtour.R;
import com.vta.virtualtour.adapters.NearMeCustomPoiAdapter;
import com.vta.virtualtour.models.MarkerInfo;

import java.util.ArrayList;
import java.util.List;

public class NearMeCustomPoiActivity extends AppCompatActivity implements NearMeCustomPoiContract.View{

    private NearMeCustomPoiContract.Presenter presenter;
    private RecyclerView recyclerView;
    private LinearLayout progressBarLayout;
    private NearMeCustomPoiAdapter nearMeCustomPoiAdapter;
    private TextView progressBarTextView;
    private List<MarkerInfo> neaMarkerInfos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me_custom_poi);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        presenter = new NearMeCustomPoiPresenter(this);
        setupViews();
        fetchNearMeCustomPoi();
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recycler_view);
        progressBarLayout = findViewById(R.id.progress_layout);
        progressBarTextView = findViewById(R.id.progress_bar_textview);
    }

    private void fetchNearMeCustomPoi() {
        presenter.showProgressBar();
        progressBarTextView.setText(getResources().getString(R.string.loading_nearby_custom_poi));
        presenter.fetchCustomPoiList();
    }

    private void setUpRecyclerListAdapter() {
        nearMeCustomPoiAdapter = new NearMeCustomPoiAdapter(neaMarkerInfos, new NearMeCustomPoiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MarkerInfo markerInfo) {

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(nearMeCustomPoiAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
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
    public void showCustomPoi(List<MarkerInfo> markerInfoArrayList) {
        neaMarkerInfos = new ArrayList<>();
        neaMarkerInfos.addAll(markerInfoArrayList);
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
