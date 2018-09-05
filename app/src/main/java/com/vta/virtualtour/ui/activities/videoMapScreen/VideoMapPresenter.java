package com.vta.virtualtour.ui.activities.videoMapScreen;

import android.location.Location;
import android.text.Spanned;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.R;
import com.vta.virtualtour.managers.NavigationManager;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.MarkerInfo;
import com.vta.virtualtour.models.Stop;
import com.vta.virtualtour.models.VideoGeoPoint;
import com.vta.virtualtour.utility.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tushar
 * Created on 08/05/18.
 */

public class VideoMapPresenter implements VideoMapContract.Presenter {

    private static final int DIRECTION_STEP_DISTANCE = 30;
    private static final int overview = 0;
    private VideoMapContract.View view;

    private Subscription subscription;
    private Observable<Long> observable;

    VideoMapPresenter(VideoMapContract.View view) {
        this.view = view;
        int POLLING_FREQUENCY = 1;
        observable = Observable.interval(POLLING_FREQUENCY, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onStopCalled() {
        view.releasePlayer();
    }

    @Override
    public void startPolling() {
        //subscription code
        if (subscription == null || subscription.isUnsubscribed()) {
            subscription = observable.subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    int duration = getDurationInSeconds(view.getCurrentVideoTime());
                    System.out.println("Video Second: " + duration);
                    view.addMarkerOnMap(duration);
                    checkForVideoEndTimeForSelectedRoute(duration);
                }
            });
        }
    }

    @Override
    public void stopPolling() {
        // UnSubscribe, if subscription is on
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    @Override
    public boolean tappedOnRoute(LatLng latLng) {
        List<VideoGeoPoint> videoGeoPoints = RouteManager.getSharedInstance().getVideoGeoPoints();
        for (int i = 0; i < videoGeoPoints.size(); i++) {
            float[] results = new float[1];
            Location.distanceBetween(videoGeoPoints.get(i).getLat(), videoGeoPoints.get(i).getLng(), latLng.latitude, latLng.longitude, results);
            float distanceInMeters = results[0];
            boolean isWithin = distanceInMeters < DIRECTION_STEP_DISTANCE;
            if (isWithin) {
                view.seekToMillis(getDurationInMilliSeconds(videoGeoPoints.get(i).getSec()));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getVideoViews() {
        return RouteManager.getSharedInstance().getVideoViews();
    }

    @Override
    public void onVideoViewChanged() {
        view.resetVideoView();
    }

    public void getRouteDetailStopsGeoPoints() {
        RouteManager.getSharedInstance().getCustomStops(new RouteManager.FetchCustomStopsListener() {
            @Override
            public void didFinishFetchingCustomStops(List<Stop> customStops) {
                view.plotRouteDetailStopsOnMap(customStops);
            }
        });
    }

    @Override
    public void findStartSecondForSelectedRoute() {
        view.seekToMillis(getDurationInMilliSeconds(RouteManager.getSharedInstance().getVideoStartSecond()));
    }

    @Override
    public void onMapNavigationButtonClick() {
        view.showMapNavigation();
    }

    @Override
    public void fullscreenButtonClicked(boolean isVideo) {
        view.showFullscreenVideoViewOrMapView(isVideo);
    }

    @Override
    public void updateNavigationText(LatLng origin, LatLng destination) {
        NavigationManager.getSharedInstance().getNavigation(origin, destination, new NavigationManager.GetNavigationListener() {
            @Override
            public void didFinishGetNavigation(String direction, String error) {

                if (!direction.equals("")) {
                    direction = direction.replace("Head", "Heading");
                    direction = direction.replace("\n", "");
                    Spanned navigationText = Utils.fromHtml("<div style=\"font-size:1.20em;font-family:'OpenSans'\">" + direction + "</div>");
                    view.showNavigationText(navigationText);
                }
            }
        });
    }

    @Override
    public void fetchRouteDetailsVideoGeoPoints(String videoViewType) {
        view.changeProgressBarText(view.getContext().getResources().getString(R.string.fetching_route_details));
        view.showProgressBar();
        RouteManager.getSharedInstance().fetchRouteDetailsVideoGeoPoints(videoViewType, RouteManager.getSharedInstance().getSelectedDirectionPosition(), new RouteManager.FetchRouteDetailsVideoGeoPointsListener() {
            @Override
            public void didFinishFetchingRouteDetailsVideoGeoPoints(List<VideoGeoPoint> videoGeoPoints, String error) {
                view.hideProgressBar();
                view.plotRouteDetailVideoGeoPointsOnMap();
            }
        });
    }


    private void checkForVideoEndTimeForSelectedRoute(int durationInSeconds) {
        if (durationInSeconds == RouteManager.getSharedInstance().getVideoEndSecond()) {
            stopPolling();
            view.hidePauseButton();
            onVideoViewChanged();
        }
    }

    //region Marker Methods

    @Override
    public void loadCustomPoi() {
        RouteManager.getSharedInstance().fetchCustomPoi(new RouteManager.FetchCustomPoiListener() {
            @Override
            public void didFinishFetchingCustomPois(List<MarkerInfo> customPois, String error) {
                view.loadCustomPoi(customPois);
            }
        });
    }

    @Override
    public void loadMeetup(LatLng carLocation) {
        RouteManager.getSharedInstance().fetchMeetups(carLocation, new RouteManager.FetchMeetupsListener() {
            @Override
            public void didFinishFetchingMeetups(List<MarkerInfo> meetups, String error) {
                view.plotMeetup(meetups);
            }
        });
    }

    @Override
    public void loadCoord(LatLng carLocation) {
        RouteManager.getSharedInstance().fetchCoords(carLocation, new RouteManager.FetchCoordsListener() {
            @Override
            public void didFinishFetchingCoords(List<MarkerInfo> coords, String error) {
                view.plotCoord(coords);
            }
        });
    }

    @Override
    public void loadLimeBike(LatLng carLocation) {

        RouteManager.getSharedInstance().fetchLimeBike(carLocation, new RouteManager.FetchLimeBikeListener() {
            @Override
            public void didFinishFetchingLimeBikes(List<MarkerInfo> limeBikes, String error) {
                view.plotLimeBikes(limeBikes);
            }
        });
    }

    @Override
    public void fetchIntegrations() {
        RouteManager.getSharedInstance().fetchIntegrations();
    }

    //endregion

    //region Helper Methods

    @Override
    public void showProgressBar() {
        view.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        view.hideProgressBar();
    }

    @Override
    public void showBufferingProgressBar() {
        view.showBufferingProgressBar();
    }

    @Override
    public void hideBufferingProgressBar() {
        view.hideBufferingProgressBar();
    }

    @Override
    public int getDurationInSeconds(int milliSeconds) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(milliSeconds);
    }

    @Override
    public int getDurationInMilliSeconds(int seconds) {
        return (int) TimeUnit.SECONDS.toMillis(seconds);
    }

    //endregion
}
