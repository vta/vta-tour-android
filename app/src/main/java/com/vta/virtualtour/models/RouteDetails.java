package com.vta.virtualtour.models;

import java.util.List;

/**
 * Created by tushar on 17/04/18.
 */

public class RouteDetails {
    private List<Stop> stops;
    private String videoId;
    private String videoUrl;
    private String videoUrlLowRes;
    private String videoBackUrl;
    private String videoBackUrlLowRes;
    private String videoLeftUrl;
    private String videoLeftUrlLowRes;
    private String videoRightUrl;
    private String videoRightUrlLowRes;

    public RouteDetails() {
    }

    public RouteDetails(List<Stop> stops, String videoId, String videoUrl, String videoUrlLowRes, String videoBackUrl, String videoBackUrlLowRes, String videoLeftUrl, String videoLeftUrlLowRes, String videoRightUrl, String videoRightUrlLowRes) {
        this.stops = stops;
        this.videoId = videoId;
        this.videoUrl = videoUrl;
        this.videoUrlLowRes = videoUrlLowRes;
        this.videoBackUrl = videoBackUrl;
        this.videoBackUrlLowRes = videoBackUrlLowRes;
        this.videoLeftUrl = videoLeftUrl;
        this.videoLeftUrlLowRes = videoLeftUrlLowRes;
        this.videoRightUrl = videoRightUrl;
        this.videoRightUrlLowRes = videoRightUrlLowRes;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoFrontUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoFrontUrlLowRes() {
        return videoUrlLowRes;
    }

    public void setVideoUrlLowRes(String videoUrlLowRes) {
        this.videoUrlLowRes = videoUrlLowRes;
    }

    public String getVideoBackUrl() {
        return videoBackUrl;
    }

    public void setVideoBackUrl(String videoBackUrl) {
        this.videoBackUrl = videoBackUrl;
    }

    public String getVideoBackUrlLowRes() {
        return videoBackUrlLowRes;
    }

    public void setVideoBackUrlLowRes(String videoBackUrlLowRes) {
        this.videoBackUrlLowRes = videoBackUrlLowRes;
    }

    public String getVideoLeftUrl() {
        return videoLeftUrl;
    }

    public void setVideoLeftUrl(String videoLeftUrl) {
        this.videoLeftUrl = videoLeftUrl;
    }

    public String getVideoLeftUrlLowRes() {
        return videoLeftUrlLowRes;
    }

    public void setVideoLeftUrlLowRes(String videoLeftUrlLowRes) {
        this.videoLeftUrlLowRes = videoLeftUrlLowRes;
    }

    public String getVideoRightUrl() {
        return videoRightUrl;
    }

    public void setVideoRightUrl(String videoRightUrl) {
        this.videoRightUrl = videoRightUrl;
    }

    public String getVideoRightUrlLowRes() {
        return videoRightUrlLowRes;
    }

    public void setVideoRightUrlLowRes(String videoRightUrlLowRes) {
        this.videoRightUrlLowRes = videoRightUrlLowRes;
    }
}
