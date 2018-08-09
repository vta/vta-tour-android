package com.vta.virtualtour.ui.activities.tutorialScreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vta.virtualtour.R;
import com.vta.virtualtour.ui.activities.BaseActivity;
import com.vta.virtualtour.ui.activities.routeChooserScreen.RouteChooserActivity;
import com.vta.virtualtour.ui.activities.routesScreen.RoutesActivity;
import com.vta.virtualtour.utility.Constants;

public class TutorialActivity extends BaseActivity implements View.OnClickListener, TutorialContract.View {

    private ViewPager viewPager;
    private TutorialViewPagerAdapter tutorialViewPagerAdapter;
    private LinearLayout layoutDots;
    private TextView[] dots;
    private int[] layouts;
    private Button buttonSkip, buttonNext;

    private TutorialContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_tutorial);

        presenter = new TutorialPresenter(this);

        setupViews();

        // making notification bar transparent
        changeStatusBarColor();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tutorial_button_skip:
                presenter.showRouteChooserScreen();
                break;

            case R.id.tutorial_button_next:
                // checking for last page
                // if last page home screen will be launched
                int current = presenter.getViewPagerItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    presenter.setTutorialDoNotShowAgain();
                    presenter.showRouteChooserScreen();
                }
                break;
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showRouteChooserScreen() {
        Intent intent = new Intent(this, RoutesActivity.class);
        startActivity(intent);
        // close this activity
        finish();
    }

    @Override
    public int getViewPagerItem(int position) {
        return viewPager.getCurrentItem() + position;
    }

    @Override
    public void changeNextButtonText(String text) {
        buttonNext.setText(text);
    }

    @Override
    public void changeSkipButtonVisibility(boolean isVisible) {
        buttonSkip.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    //region private methods
    private void setupViews() {
        viewPager = findViewById(R.id.tutorial_view_pager);
        layoutDots = findViewById(R.id.tutorial_layout_dots);
        buttonSkip = findViewById(R.id.tutorial_button_skip);
        buttonSkip.setOnClickListener(this);
        buttonNext = findViewById(R.id.tutorial_button_next);
        buttonNext.setOnClickListener(this);

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.tutorial_slide1,
                R.layout.tutorial_slide2,
                R.layout.tutorial_slide3};

        // adding bottom dots
        addBottomDots(0);

        tutorialViewPagerAdapter = new TutorialViewPagerAdapter(this, layouts);
        viewPager.setAdapter(tutorialViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    //endregion

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                presenter.changeNextButtonText(getString(R.string.start));
                presenter.changeSkipButtonVisibility(false);
            } else {
                // still pages are left
                presenter.changeNextButtonText(getString(R.string.next));
                presenter.changeSkipButtonVisibility(true);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


}
