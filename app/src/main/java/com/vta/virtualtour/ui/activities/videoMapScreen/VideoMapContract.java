package com.vta.virtualtour.ui.activities.videoMapScreen;

import android.content.Context;

import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.vta.virtualtour.models.MarkerInfo;
import com.vta.virtualtour.models.Stop;
import com.vta.virtualtour.models.VideoGeoPoint;

import java.util.List;

import java.util.List;

/**
 * Created by tushar on 08/05/18.
 */

public interface VideoMapContract {

    interface View {
        Context getContext();
        int getCurrentVideoTime();
        int getVideoDuration();
        void releasePlayer();
        void addMarkerOnMap(int videoSecond);
        void seekToMillis(int milliSeconds);
        void showBufferingProgressBar();
        void hideBufferingProgressBar();
        void resetVideoView();
        void plotRouteDetailStopsOnMap(List<Stop> stops);
        void stopVideo();
        void showMapNavigation();
        void setNavigationDetailText(String detail);
        void showFullscreenVideoViewOrMapView(boolean isVideo);
        void loadPoiNearStop(List<MarkerInfo> poiList);
        void loadCustomPoi(List<MarkerInfo> customPoiList);
        void plotMeetup(List<MarkerInfo> meetupList);
        void plotCoord(List<MarkerInfo> coordList);
        void showProgressBar();
        void hideProgressBar();
        void hidePauseButton();
        void plotRouteDetailVideoGeoPointsOnMap();
        void changeProgressBarText(String progressBarText);
    }

    interface Presenter {
        void showBufferingProgressBar();
        void hideBufferingProgressBar();
        void onStopCalled();
        void startPolling();
        void stopPolling();
        int getDurationInSeconds(int milliSeconds);
        int getDurationInMilliSeconds(int seconds);
        boolean tappedOnRoute(LatLng latLng);
        List<String> getVideoViews();
        void onVideoViewChanged();
        void getRouteDetailStopsGeoPoints();
        void findStartSecondForSelectedRoute();
        void onMapNavigationButtonClick();
        void fullscreenButtonClicked(boolean isVideo);
        void fetchDirectionDetails(com.google.maps.model.LatLng origin, com.google.maps.model.LatLng destination, TravelMode mode);
        void getDirectionStep(LatLng position);
        void loadPoiNearCar(LatLng carLocation);
        void loadCustomPoi();
        void loadMeetup(LatLng carLocation);
        void loadCoord(LatLng carLocation);
        void showProgressBar();
        void hideProgressBar();
        void fetchIntegrations();
        void fetchRouteDetailsVideoGeoPoints(String videoViewType);
    }
}
