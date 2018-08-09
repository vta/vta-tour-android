package com.vta.virtualtour.services;

import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.Stop;

/**
 * Created by tushar on 17/04/18.
 */

public interface RouteServiceInterface {
    void fetchRoutes(RouteManager.FetchRoutesListener fetchRoutesListener);
    void fetchRouteDetails(String code, String direction, RouteManager.FetchRouteDetailsListener fetchRouteDetailsListener);
    void fetchRouteDetailsVideoGeoPoints(String videoViewType, String code, String direction, RouteManager.FetchRouteDetailsVideoGeoPointsListener fetchRouteDetailsVideoGeoPointsListener);
    void fetchIntegrations(RouteManager.FetchIntegrationsListener fetchIntegrationsListener);
    void fetchAmenitiesForStop(Stop stop, RouteManager.FetchAmenitiesForStopListener fetchAmenitiesForStopListener);
    void fetchCustomPoi(RouteManager.FetchCustomPoiListener fetchCustomPoiListener);
}
