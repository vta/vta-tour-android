package com.vta.virtualtour.ui.activities.navigationScreen;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.vta.virtualtour.managers.NavigationManager;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.Stop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tushar
 * Created on 6/10/2018.
 */
public class NavigationPresenter implements NavigationContract.Presenter {

    private static final int overview = 0;
    private NavigationContract.View view;

    NavigationPresenter(NavigationContract.View view) {
        this.view = view;
    }

    @Override
    public void getDirectionDetails(TravelMode mode) {

        final List<LatLng> points = new ArrayList<>();
        final List<String> stops = new ArrayList<>();

        // Get All stops for stop by stop navigation
        for (Stop stop : RouteManager.getSharedInstance().getCustomStops()) {
            com.google.maps.model.LatLng point = new com.google.maps.model.LatLng(stop.getLat(), stop.getLng());
            points.add(point);
            stops.add(stop.getName());
        }

        StringBuilder directions = new StringBuilder();

        for (int i = 0; i < points.size() - 1; i++) {
            DirectionsResult results = NavigationManager.getSharedInstance().getDirectionsDetails(points.get(i), points.get(i + 1), mode);
            view.updateMapView(results);
            if (results != null) {
                DirectionsStep[] directionsSteps = results.routes[overview].legs[overview].steps;

                // Add Header for the Stops Names
                if (i < stops.size() - 1) {
                    directions.append("<b>").append(stops.get(i)).append(" - ").append(stops.get(i + 1)).append("</b> <br/> <br/>");
                }

                directionsSteps.clone();
                if (directionsSteps.length > 0) {
                    for (DirectionsStep step : directionsSteps) {
                        NavigationManager.getSharedInstance().getDirectionsStepList().add(step);

                        // Avoid Last entry that says "Destination has arrived"
                        int indexOfDestination = step.htmlInstructions.indexOf("Destination");
                        if (indexOfDestination >= 0) {
                            directions.append(step.htmlInstructions.substring(0, indexOfDestination));
                        } else {
                            directions.append(step.htmlInstructions);
                        }

                        if (!step.htmlInstructions.endsWith("</div>")) {
                            directions.append("<br/><br/>");
                        }
                    }
                }
            }
        }

        view.setDirections(directions.toString());
    }

    @Override
    public void fullscreenButtonClicked() {
        view.showFullscreenMapView();
    }
}
