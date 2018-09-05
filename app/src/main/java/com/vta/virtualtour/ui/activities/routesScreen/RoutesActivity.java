package com.vta.virtualtour.ui.activities.routesScreen;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.vta.virtualtour.R;
import com.vta.virtualtour.ui.activities.BaseActivity;
import com.vta.virtualtour.ui.activities.videoMapScreen.VideoMapActivity;
import com.vta.virtualtour.ui.fragments.nearMeScreen.NearMeFragment;
import com.vta.virtualtour.ui.fragments.virtualTourScreen.VirtualTourFragment;
import com.vta.virtualtour.utility.Constants;
import com.vta.virtualtour.utility.FetchCurrentLocation;

public class RoutesActivity extends BaseActivity implements RoutesContract.View,
        VirtualTourFragment.VirtualTourFragmentInteractionListener, FetchCurrentLocation.FetchCurrentLocationListener, TabLayout.OnTabSelectedListener {

    private static final String TAG = "RoutesActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private RoutesContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        presenter = new RoutesPresenter(this);
        setupViews();
    }

    private void setupViews() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onViewRouteClicked(String direction, String routeName) {
        Intent intent = new Intent(RoutesActivity.this, VideoMapActivity.class);
        intent.putExtra("Direction", direction);
        intent.putExtra("RouteName", routeName);
        startActivity(intent);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constants.PERMISSION_REQUEST_CODE: {
                Fragment activeFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "Permission granted");
                    if (mViewPager.getCurrentItem() == 0 && activeFragment != null) {
                        ((VirtualTourFragment) activeFragment).changeCheckBoxState(true);
                    }else if (mViewPager.getCurrentItem() == 1 && activeFragment != null) {
                        ((NearMeFragment) activeFragment).locationPermissionGranted();
                    }
                } else {
                    Log.d("TAG", "Permission denied");
                    if (mViewPager.getCurrentItem() == 0 && activeFragment != null) {
                        ((VirtualTourFragment) activeFragment).changeCheckBoxState(false);
                    }
                }
                if (mViewPager.getCurrentItem() == 0 && activeFragment != null) {
                    ((VirtualTourFragment) activeFragment).locationPermissionGranted();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment activeFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case Constants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("TAG", "User agreed to make required location settings changes.");
                        if (mViewPager.getCurrentItem() == 0 && activeFragment != null) {
                            ((VirtualTourFragment) activeFragment).checkForLocationPermission();
                        }else if (mViewPager.getCurrentItem() == 1 && activeFragment != null){
                            ((NearMeFragment) activeFragment).checkForLocationPermission();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("TAG", "User chose not to make required location settings changes.");
                       // based on the current position you can then cast the page to the correct
                        // class and call the method:
                        if (mViewPager.getCurrentItem() == 0 && activeFragment != null) {
                            ((VirtualTourFragment) activeFragment).changeCheckBoxState(false);
                        }

                        break;
                }
                break;
        }
    }

    @Override
    public void currentLatLong(String latitude, String longitude) {
        Log.d(TAG,"Location found");
        Fragment activeFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
        if (mViewPager.getCurrentItem() == 0 && activeFragment != null) {
            ((VirtualTourFragment) activeFragment).currentLatLong(latitude,longitude);
        }else if(mViewPager.getCurrentItem() == 1 && activeFragment != null){
            ((NearMeFragment) activeFragment).currentLatLong(latitude,longitude);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 1){
            mViewPager.setCurrentItem(1);
            Fragment activeFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
            ((NearMeFragment) activeFragment).displayLocationSettingsRequest(this);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return VirtualTourFragment.newInstance();
                case 1:
                    return NearMeFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_virtual_tour);
                case 1:
                    return getString(R.string.title_near_me);
            }
            return null;
        }
    }
}
