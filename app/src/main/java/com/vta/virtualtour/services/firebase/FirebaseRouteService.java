package com.vta.virtualtour.services.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.Amenities;
import com.vta.virtualtour.models.AmenitiesFields;
import com.vta.virtualtour.models.Integrations;
import com.vta.virtualtour.models.MarkerInfo;
import com.vta.virtualtour.models.Route;
import com.vta.virtualtour.models.RouteDetails;
import com.vta.virtualtour.models.Stop;
import com.vta.virtualtour.models.VideoGeoPoint;
import com.vta.virtualtour.services.RouteServiceInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tushar on 17/04/18.
 */

public class FirebaseRouteService implements RouteServiceInterface {

    private DatabaseReference mDatabase;

    @Override
    public void fetchRoutes(final RouteManager.FetchRoutesListener fetchRoutesListener) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Route> routes = new ArrayList<>();

                for (DataSnapshot deviceSnapshot : dataSnapshot.getChildren()) {
                    Route route = deviceSnapshot.getValue(Route.class);
                    routes.add(route);
                }

                fetchRoutesListener.didFinishFetchingRoutes(routes, "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fetchRoutesListener.didFinishFetchingRoutes(null, "Some error occurred.");
            }
        };

        mDatabase.child("routes").addValueEventListener(valueEventListener);
    }

    @Override
    public void fetchRouteDetails(String code, String direction, final RouteManager.FetchRouteDetailsListener fetchRouteDetailsListener) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot stopsDataSnapshot = dataSnapshot.child("stops");
                List<Stop> stops = new ArrayList<>();
                for (DataSnapshot stopSnapshot : stopsDataSnapshot.getChildren()) {
                    Stop stop = stopSnapshot.getValue(Stop.class);
                    stops.add(stop);
                }
                fetchRouteDetailsListener.didFinishFetchingRouteDetails(new RouteDetails(stops, dataSnapshot.child("videoId").getValue() == null? "false": dataSnapshot.child("videoId").getValue().toString(), dataSnapshot.child("videoUrl").getValue().toString(), dataSnapshot.child("videoUrlLowRes").getValue().toString(), dataSnapshot.child("videoBackUrlLowRes").getValue().toString(), dataSnapshot.child("videoLeftUrl").getValue().toString(), dataSnapshot.child("videoLeftUrlLowRes").getValue().toString(), dataSnapshot.child("videoNightUrl").getValue().toString(), dataSnapshot.child("videoRightUrl").getValue().toString(), dataSnapshot.child("videoRightUrlLowRes").getValue().toString()), "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fetchRouteDetailsListener.didFinishFetchingRouteDetails(null, "Some error occurred.");
            }
        };

        mDatabase.child("route-details").child(code).child(direction).addValueEventListener(valueEventListener);
    }

    @Override
    public void fetchRouteDetailsVideoGeoPoints(String videoViewType, String code, String direction, final RouteManager.FetchRouteDetailsVideoGeoPointsListener fetchRouteDetailsVideoGeoPointsListener) {
        FirebaseDatabase.getInstance().getReference().child(videoViewType).child(code).child(direction).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<VideoGeoPoint> videoGeoPoints = new ArrayList<>();
                for (DataSnapshot videoGeoPointSnapshot : dataSnapshot.getChildren()) {
                    VideoGeoPoint videoGeoPoint = videoGeoPointSnapshot.getValue(VideoGeoPoint.class);
                    videoGeoPoint.setSec(Integer.parseInt(videoGeoPointSnapshot.getKey()));
                    videoGeoPoints.add(videoGeoPoint);
                }
                fetchRouteDetailsVideoGeoPointsListener.didFinishFetchingRouteDetailsVideoGeoPoints(videoGeoPoints, "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fetchRouteDetailsVideoGeoPointsListener.didFinishFetchingRouteDetailsVideoGeoPoints(null, "Some error occurred.");
            }
        });
    }

    @Override
    public void fetchIntegrations(final RouteManager.FetchIntegrationsListener fetchIntegrationsListener) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("integrations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integrations integrations = dataSnapshot.getValue(Integrations.class);
                fetchIntegrationsListener.didFinishFetchingIntegrations(integrations, "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fetchIntegrationsListener.didFinishFetchingIntegrations(null, "Some error occurred.");
            }
        });
    }

    @Override
    public void fetchAmenitiesForStop(Stop stop, final RouteManager.FetchAmenitiesForStopListener fetchAmenitiesForStopListener) {
        FirebaseDatabase.getInstance().getReference().child("amenities").child(stop.getCode()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot listDataSnapshot = dataSnapshot.child("list");
                List<AmenitiesFields> amenitiesFieldsList = new ArrayList<>();
                for (DataSnapshot snapshot : listDataSnapshot.getChildren()) {
                    AmenitiesFields amenitiesFields = snapshot.getValue(AmenitiesFields.class);
                    amenitiesFieldsList.add(amenitiesFields);
                }
                fetchAmenitiesForStopListener.didFinishFetchingAmenitiesForStop(new Amenities(amenitiesFieldsList, dataSnapshot.child("stop_code").getValue().toString()), "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fetchAmenitiesForStopListener.didFinishFetchingAmenitiesForStop(null, "Some error occurred.");
            }
        });
    }

    @Override
    public void fetchCustomPoi(final RouteManager.FetchCustomPoiListener fetchCustomPoiListener) {
        FirebaseDatabase.getInstance().getReference().child("customPois").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MarkerInfo> markerInfoList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String description = snapshot.child("description").getValue() == null ? "" : snapshot.child("description").getValue().toString();
                    String icon = snapshot.child("icon").getValue() == null ? "" : snapshot.child("icon").getValue().toString();
                    markerInfoList.add(new MarkerInfo(snapshot.child("name").getValue().toString(), snapshot.child("address").getValue().toString() + "\n" + description, Double.parseDouble(snapshot.child("latitude").getValue().toString()), Double.parseDouble(snapshot.child("longitude").getValue().toString()), icon));
                }
                fetchCustomPoiListener.didFinishFetchingCustomPois(markerInfoList, "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fetchCustomPoiListener.didFinishFetchingCustomPois(null, "Some error occurred.");
            }
        });
    }
}
