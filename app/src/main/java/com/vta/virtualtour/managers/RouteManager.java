package com.vta.virtualtour.managers;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.R;
import com.vta.virtualtour.VirtualTourApplication;
import com.vta.virtualtour.interfaces.AsyncTaskJsonResultListener;
import com.vta.virtualtour.models.Amenities;
import com.vta.virtualtour.models.AmenitiesFields;
import com.vta.virtualtour.models.Integrations;
import com.vta.virtualtour.models.MarkerInfo;
import com.vta.virtualtour.models.NearByPlaceOfInterest;
import com.vta.virtualtour.models.PlaceOfInterest;
import com.vta.virtualtour.models.Route;
import com.vta.virtualtour.models.RouteDetails;
import com.vta.virtualtour.models.Stop;
import com.vta.virtualtour.models.VideoGeoPoint;
import com.vta.virtualtour.services.RouteServiceInterface;
import com.vta.virtualtour.services.firebase.FirebaseRouteService;
import com.vta.virtualtour.utility.JSONAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tushar
 * Created on 17/04/18.
 */

public class RouteManager {
    public interface FetchRoutesListener {
        void didFinishFetchingRoutes(List<Route> routes, String error);
    }

    public interface FetchRouteDetailsListener {
        void didFinishFetchingRouteDetails(RouteDetails routeDetails, String error);
    }

    public interface FetchRouteDetailsVideoGeoPointsListener {
        void didFinishFetchingRouteDetailsVideoGeoPoints(List<VideoGeoPoint> videoGeoPoints, String error);
    }

    public interface FetchMeetupsListener {
        void didFinishFetchingMeetups(List<MarkerInfo> meetups, String error);
    }

    public interface FetchCoordsListener {
        void didFinishFetchingCoords(List<MarkerInfo> coords, String error);
    }

    public interface FetchPoiListener {
        void didFinishFetchingPois(List<MarkerInfo> pois, String error);
    }

    public interface FetchCompletePoiDetailsListener {
        void didFinishFetchingCompletePoiDetails(JSONArray jsonArray, String error);
    }

    public interface FetchIntegrationsListener {
        void didFinishFetchingIntegrations(Integrations integrations, String error);
    }

    public interface FetchPlaceDetailsListener {
        void didFinishFetchingPlacesDetails(PlaceOfInterest placeOfInterest, String error);
    }

    public interface FetchAmenitiesForStopListener {
        void didFinishFetchingAmenitiesForStop(Amenities amenities, String error);
    }

    public interface FetchCustomPoiListener {
        void didFinishFetchingCustomPois(List<MarkerInfo> customPois, String error);
    }

    public interface FetchCustomStopsListener {
        void didFinishFetchingCustomStops(List<Stop> customStops);
    }

    public interface FetchDistanceTimeToPoiListener {
        void didFinishFetchDistanceTimeToPoi(String distanceInmetre, String timeInMinutes, String error);
    }

    private static RouteManager routeManager;
    private Context context;
    private List<Route> routes;
    private List<Route> nearMeRoutes;
    private Route route;
    private Route nearMeRoute;
    private RouteDetails routeDetails;
    private List<VideoGeoPoint> videoGeoPoints;
    private int selectedDirectionPosition;
    private Stop selectedDeparture;
    private Stop selectedDestination;
    private Integrations integrations;
    private boolean isCurrentLocationCheckBoxChecked = false;
    private List<NearByPlaceOfInterest> nearByPlaceOfInterests;
    private List<Stop> nearMeStopList ;
    private List<Stop> customStops ;

    public static RouteManager getSharedInstance() {
        if (routeManager == null) {
            routeManager = new RouteManager();
            routeManager.context = VirtualTourApplication.getAppContext();
        }
        return routeManager;
    }

    public void clearInstance() {
        routeManager = null;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Route> getNearMeRoutes() {
        return nearMeRoutes;
    }

    public void setNearMeRoutes(List<Route> nearMeRoutes) {
        this.nearMeRoutes = nearMeRoutes;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public RouteDetails getRouteDetails() {
        return routeDetails;
    }

    public void setRouteDetails(RouteDetails routeDetails) {
        this.routeDetails = routeDetails;
    }

    public List<VideoGeoPoint> getVideoGeoPoints() {
        return videoGeoPoints;
    }

    public void setVideoGeoPoints(List<VideoGeoPoint> videoGeoPoints) {
        this.videoGeoPoints = videoGeoPoints;
    }

    public Route getNearMeRoute() {
        return nearMeRoute;
    }

    public void setNearMeRoute(Route nearMeRoute) {
        this.nearMeRoute = nearMeRoute;
    }

    public int getSelectedDirectionPosition() {
        return selectedDirectionPosition;
    }

    public void setSelectedDirectionPosition(int selectedDirectionPosition) {
        this.selectedDirectionPosition = selectedDirectionPosition;
    }

    public Stop getSelectedDeparture() {
        return selectedDeparture;
    }

    public void setSelectedDeparture(Stop selectedDeparture) {
        this.selectedDeparture = selectedDeparture;
    }

    public Stop getSelectedDestination() {
        return selectedDestination;
    }

    public void setSelectedDestination(Stop selectedDestination) {
        this.selectedDestination = selectedDestination;
    }

    public Integrations getIntegrations() {
        return integrations;
    }

    public void setIntegrations(Integrations integrations) {
        this.integrations = integrations;
    }

    public boolean isCurrentLocationCheckBoxChecked() {
        return isCurrentLocationCheckBoxChecked;
    }

    public void setCurrentLocationCheckBoxChecked(boolean currentLocationCheckBoxChecked) {
        isCurrentLocationCheckBoxChecked = currentLocationCheckBoxChecked;
    }

    public List<NearByPlaceOfInterest> getNearByPlaceOfInterests() {
        return nearByPlaceOfInterests;
    }

    public void setNearByPlaceOfInterests(List<NearByPlaceOfInterest> nearByPlaceOfInterests) {
        this.nearByPlaceOfInterests = nearByPlaceOfInterests;
    }

    public List<Stop> getNearMeStopList() {
        return nearMeStopList;
    }

    public void setNearMeStopList(List<Stop> nearMeStopList) {
        this.nearMeStopList = nearMeStopList;
    }

    public List<Stop> getCustomStops() {
        return customStops;
    }

    public void setCustomStops(List<Stop> customStops) {
        this.customStops = customStops;
    }

    public void fetchRoutes(final FetchRoutesListener fetchRoutesListener) {
        getRoutesService().fetchRoutes(new FetchRoutesListener() {
            @Override
            public void didFinishFetchingRoutes(List<Route> routes, String error) {
                fetchRoutesListener.didFinishFetchingRoutes(routes, error);
            }
        });
    }

    public void fetchRouteDetails(int position, final FetchRouteDetailsListener fetchRouteDetailsListener) {
        getRoutesService().fetchRouteDetails(getRoute() == null? getNearMeRoute().getCode():getRoute().getCode(), position == 0 ? "a" : "b", new FetchRouteDetailsListener() {
            @Override
            public void didFinishFetchingRouteDetails(RouteDetails routeDetails, String error) {
                fetchRouteDetailsListener.didFinishFetchingRouteDetails(routeDetails, error);
            }
        });
    }

    public void fetchRouteDetailsVideoGeoPoints(String videoViewType, int position, final RouteManager.FetchRouteDetailsVideoGeoPointsListener fetchRouteDetailsVideoGeoPointsListener) {
        getRoutesService().fetchRouteDetailsVideoGeoPoints(videoViewType, getRoute().getCode(), position == 0 ? "a" : "b", new FetchRouteDetailsVideoGeoPointsListener() {
            @Override
            public void didFinishFetchingRouteDetailsVideoGeoPoints(List<VideoGeoPoint> videoGeoPoints, String error) {
                setVideoGeoPoints(videoGeoPoints);
                fetchRouteDetailsVideoGeoPointsListener.didFinishFetchingRouteDetailsVideoGeoPoints(videoGeoPoints, error);
            }
        });
    }

    public void fetchIntegrations() {
        getRoutesService().fetchIntegrations(new FetchIntegrationsListener() {
            @Override
            public void didFinishFetchingIntegrations(Integrations integrations, String error) {
                setIntegrations(integrations);
            }
        });
    }

    private RouteServiceInterface getRoutesService() {
        FirebaseRouteService routeService = new FirebaseRouteService();
        return routeService;
    }

    public List<String> getCustomRoutes(List<Route> routes) {
        setRoutes(routes);
        List<String> customRoutes = new ArrayList<>();
        for (Route route : routes) {
            customRoutes.add(route.getName());
        }
        return customRoutes;
    }

    public List<String> getCustomNearMeRoutes(List<Route> routes) {
        setNearMeRoutes(routes);
        List<String> customRoutes = new ArrayList<>();
        for (Route route : routes) {
            customRoutes.add(route.getName());
        }
        return customRoutes;
    }

    public List<String> getCustomDirections(int position) {
        setRoute(getRoutes().get(position));
        List<String> customDirections = new ArrayList<>();

        customDirections.add(Character.toUpperCase(getRoute().getDirectionAName().charAt(0)) + getRoute().getDirectionAName().substring(1));
        customDirections.add(Character.toUpperCase(getRoute().getDirectionBName().charAt(0)) + getRoute().getDirectionBName().substring(1));
        return customDirections;
    }

    public List<String> getCustomNearByDirections(int position) {
        setNearMeRoute(getNearMeRoutes().get(position));
        List<String> customDirections = new ArrayList<>();

        customDirections.add(Character.toUpperCase(getNearMeRoute().getDirectionAName().charAt(0)) + getNearMeRoute().getDirectionAName().substring(1));
        customDirections.add(Character.toUpperCase(getNearMeRoute().getDirectionBName().charAt(0)) + getNearMeRoute().getDirectionBName().substring(1));
        return customDirections;
    }

    public List<String> getCustomStops(RouteDetails routeDetails) {
        List<String> customStops = new ArrayList<>();
        List<Stop> stops = routeDetails.getStops();
        for (Stop stop : stops) {
            customStops.add(stop.getName());
        }
        return customStops;
    }

    public void setDeparture(int position) {
        setSelectedDeparture(getRouteDetails().getStops().get(position));
    }

    public void setDestination(int position) {
        setSelectedDestination(getRouteDetails().getStops().get(position));
    }

    public List<String> getVideoViews() {
        return Arrays.asList(context.getResources().getStringArray(R.array.views));
    }

    public List<VideoGeoPoint> getCustomVideoGeoPoints() {
        List<VideoGeoPoint> customVideoGeoPoints = new ArrayList<>();
        int departureSec = RouteManager.getSharedInstance().getSelectedDeparture().getSec();
        int destinationSec = RouteManager.getSharedInstance().getSelectedDestination().getSec();
        int greaterValue = departureSec < destinationSec ? destinationSec : departureSec;
        int smallerValue = departureSec > destinationSec ? destinationSec : departureSec;
        List<VideoGeoPoint> videoGeoPoints = RouteManager.getSharedInstance().getVideoGeoPoints();
        for (int i = smallerValue; i < videoGeoPoints.size(); i++) {
            customVideoGeoPoints.add(videoGeoPoints.get(i));
            if (videoGeoPoints.get(i).getSec() == greaterValue) {
                break;
            }
        }
        return customVideoGeoPoints;
    }

    public void getCustomStops(final FetchCustomStopsListener fetchCustomStopsListener) {
        final List<Stop> customStops = new ArrayList<>();
        List<Stop> stopList = RouteManager.getSharedInstance().getRouteDetails().getStops();
        List<VideoGeoPoint> customVideoGeoPoints = getCustomVideoGeoPoints();
        for (int i = 0; i < stopList.size(); i++) {
            for (int j = 0; j < customVideoGeoPoints.size(); j++) {
                if (customVideoGeoPoints.get(j).getSec() == stopList.get(i).getSec()) {
                    customStops.add(stopList.get(i));
                }
            }
        }
        fetchAmenities(customStops, fetchCustomStopsListener);
    }

    private void fetchAmenities(final List<Stop> customStops, final FetchCustomStopsListener fetchCustomStopsListener) {
        for (final Stop stop : customStops) {
            getRoutesService().fetchAmenitiesForStop(stop, new FetchAmenitiesForStopListener() {
                @Override
                public void didFinishFetchingAmenitiesForStop(Amenities amenities, String error) {
                    stop.setAmenities(getAmenitiesText(amenities));
                    if (didFinishFetchingAmenities(customStops)) {
                        setCustomStops(customStops);
                        fetchCustomStopsListener.didFinishFetchingCustomStops(customStops);
                    }
                }
            });
        }
    }

    private boolean didFinishFetchingAmenities(List<Stop> customStops) {
        for (Stop customStop : customStops) {
            if (customStop.getAmenities() == null)
                return false;
        }
        return true;
    }

    private String getAmenitiesText(Amenities amenities) {
        String amenitiesText = "";
        for (AmenitiesFields amenitiesFields : amenities.getList()) {
            // Amenities are added only if the key is not "stop_id" and value is not 0
            if (!amenitiesFields.getKey().equals("stop_id") && !amenitiesFields.getValue().equals("0") && !amenitiesFields.getValue().equals("")) {
                if (!amenitiesText.equals("")) {
                    amenitiesText += ", ";
                }
                // Only key is appended if value = 1 else key: value
                if (amenitiesFields.getValue().equals("1")) {
                    amenitiesText = amenitiesText + amenitiesFields.getKey();
                } else {
                    amenitiesText = amenitiesText + amenitiesFields.getKey() + ": " + amenitiesFields.getValue();
                }
            }
        }
        return amenitiesText;
    }

    public int getVideoStartSecond() {
        List<VideoGeoPoint> videoGeoPoints = RouteManager.getSharedInstance().getCustomVideoGeoPoints();
        if (videoGeoPoints.size() > 0) {
            return videoGeoPoints.get(0).getSec();
        }
        return 0;
    }

    public int getVideoEndSecond() {
        List<VideoGeoPoint> videoGeoPoints = RouteManager.getSharedInstance().getCustomVideoGeoPoints();
        if (videoGeoPoints.size() > 0) {
            return videoGeoPoints.get(videoGeoPoints.size() - 1).getSec();
        }
        return 0;
    }

    public void clearRouteChooserFields() {
        setRoute(null);
        setRouteDetails(null);
        setSelectedDeparture(null);
        setSelectedDestination(null);
    }

    public void fetchPoi(LatLng carLocation, final FetchPoiListener fetchPoiListener) {
        new JSONAsyncTask(new AsyncTaskJsonResultListener() {
            @Override
            public void asyncTaskResult(JSONObject jsonObject) {
                // Mapping

                List<MarkerInfo> poiList = new ArrayList<MarkerInfo>();
                try {
                    if (jsonObject != null) {
                        JSONArray resultsArray = jsonObject.getJSONArray("results");
                        for (int i = 0; i < resultsArray.length(); i++) {
                            JSONObject data = resultsArray.getJSONObject(i);
                            JSONObject jsonObjectGeometry = data.getJSONObject("geometry");
                            JSONObject jsonObjectLocation = jsonObjectGeometry.getJSONObject("location");

                            poiList.add(new MarkerInfo(data.getString("name"), data.getString("vicinity"), jsonObjectLocation.getDouble("lat"), jsonObjectLocation.getDouble("lng"), ""));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fetchPoiListener.didFinishFetchingPois(poiList, null);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + carLocation.latitude + "," + carLocation.longitude + "&radius=500&key=" + context.getResources().getString(R.string.api_key));
    }

    public void fetchCompletePoiDetails(LatLng currentLocation,int radius, final FetchCompletePoiDetailsListener fetchCompletePoiDetailsListener) {
        new JSONAsyncTask(new AsyncTaskJsonResultListener() {
            @Override
            public void asyncTaskResult(JSONObject jsonObject) {
                // Mapping
                JSONArray resultsArray = null;
                try {
                    if (jsonObject != null) {
                        resultsArray = jsonObject.getJSONArray("results");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fetchCompletePoiDetailsListener.didFinishFetchingCompletePoiDetails(resultsArray, null);
            }
        }).execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + currentLocation.latitude + "," + currentLocation.longitude + "&radius="+radius+"&key=" + context.getResources().getString(R.string.api_key));
    }

    public void fetchMeetups(LatLng carLocation, final FetchMeetupsListener fetchMeetupsListener) {
        new JSONAsyncTask(new AsyncTaskJsonResultListener() {
            @Override
            public void asyncTaskResult(JSONObject jsonObject) {
                List<MarkerInfo> meetupList = new ArrayList<MarkerInfo>();
                try {
                    if (jsonObject != null) {
                        JSONArray resultsArray = jsonObject.getJSONArray("results");
                        for (int i = 0; i < resultsArray.length(); i++) {
                            JSONObject data = resultsArray.getJSONObject(i);
                            meetupList.add(new MarkerInfo(data.getString("name"), "Members: " + data.getString("members"), data.getDouble("lat"), data.getDouble("lon"), ""));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fetchMeetupsListener.didFinishFetchingMeetups(meetupList, null);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://api.meetup.com/2/groups/?lat=" + carLocation.latitude + "&lon=" + carLocation.longitude + "&key=" + getIntegrations().getMeetUpApiKey() + "&radius=5");
    }

    public void fetchCoords(LatLng carLocation, final FetchCoordsListener fetchCoordsListener) {
        new JSONAsyncTask(new AsyncTaskJsonResultListener() {
            @Override
            public void asyncTaskResult(JSONObject jsonObject) {
                List<MarkerInfo> coordList = new ArrayList<MarkerInfo>();
                try {
                    if (jsonObject != null) {
                        JSONArray featuresArray = jsonObject.getJSONArray("features");
                        for (int i = 0; i < featuresArray.length(); i++) {
                            JSONObject data = featuresArray.getJSONObject(i);
                            JSONObject propertiesObject = data.getJSONObject("properties");
                            coordList.add(new MarkerInfo(propertiesObject.getString("name"), "Bikes Available: " + propertiesObject.getString("num_bikes_available") + ", Docks Available: " + propertiesObject.getString("num_docks_available"), propertiesObject.getDouble("lat"), propertiesObject.getDouble("lon"), ""));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fetchCoordsListener.didFinishFetchingCoords(coordList, null);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://api.coord.co/v1/bike/location?latitude=" + carLocation.latitude + "&longitude=" + carLocation.longitude + "&radius_km=5&access_key=" + getIntegrations().getCoordApiKey());
    }

    public void fetchPlaceDetails(final String placeId, final String photoUrl, final LatLng currentLocation, final LatLng placeLocation, final FetchPlaceDetailsListener fetchPlaceDetailsListener) {
        final PlaceOfInterest placeOfInterest = new PlaceOfInterest();
        new JSONAsyncTask(new AsyncTaskJsonResultListener() {
            @Override
            public void asyncTaskResult(JSONObject jsonObject) {
                JSONObject placeDetails = null;
                try {
                    if (jsonObject != null) {
                        placeDetails = jsonObject.getJSONObject("result");
                        float rating = 0;
                        String address = "";
                        String phoneNumber = "";
                        String availability = "Available";
                        String website = "";
                        if (placeDetails.has("formatted_address")) {
                            address = placeDetails.getString("formatted_address");
                        }
                        if (placeDetails.has("permanently_closed")) {
                            availability = "Closed Now";
                        }
                        if (placeDetails.has("formatted_phone_number")) {
                            phoneNumber = placeDetails.getString("formatted_phone_number");
                        }
                        if (placeDetails.has("website")) {
                            website = placeDetails.getString("website");
                        }
                        if (placeDetails.has("rating")) {
                            rating = Float.parseFloat(placeDetails.getString("rating"));
                        }

                        placeOfInterest.setId(placeId);
                        placeOfInterest.setName(placeDetails.getString("name"));
                        placeOfInterest.setAddress(address);
                        placeOfInterest.setAvailability(availability);
                        placeOfInterest.setCurrentLatLong(currentLocation);
                        placeOfInterest.setImageUrl(photoUrl);
                        placeOfInterest.setPhone(phoneNumber);
                        placeOfInterest.setPlaceLatLong(placeLocation);
                        placeOfInterest.setRatings(rating);
                        placeOfInterest.setWebsite(website);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fetchPlaceDetailsListener.didFinishFetchingPlacesDetails(placeOfInterest, null);
            }
        }).execute("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&fields=geometry,name,rating,formatted_address,formatted_phone_number,website,permanently_closed&key=" + context.getResources().getString(R.string.api_key));
    }

    public void fetchCustomPoi(final FetchCustomPoiListener fetchCustomPoiListener) {
        getRoutesService().fetchCustomPoi(new FetchCustomPoiListener() {
            @Override
            public void didFinishFetchingCustomPois(List<MarkerInfo> customPois, String error) {
                fetchCustomPoiListener.didFinishFetchingCustomPois(customPois, error);
            }
        });
    }

    public void fetchDistanceTimeToPoi(LatLng currentLocation, LatLng poiLocation, final FetchDistanceTimeToPoiListener fetchDistanceTimeToPoiListener) {
        new JSONAsyncTask(new AsyncTaskJsonResultListener() {
            @Override
            public void asyncTaskResult(JSONObject jsonObject) {
                try {
                    JSONArray routesArray = jsonObject.getJSONArray("routes");
                    if (routesArray.length() > 0) {
                        JSONObject object = routesArray.getJSONObject(0);
                        JSONArray legsArray = object.getJSONArray("legs");
                        if (legsArray.length() > 0) {
                            JSONObject legsObject = legsArray.getJSONObject(0);
                            JSONObject distanceObject = legsObject.getJSONObject("distance");
                            JSONObject durationObject = legsObject.getJSONObject("duration");

                            fetchDistanceTimeToPoiListener.didFinishFetchDistanceTimeToPoi(distanceObject.getString("value"), durationObject.getString("text"), null);
                        }
                    }
                } catch (JSONException e) {
                    fetchDistanceTimeToPoiListener.didFinishFetchDistanceTimeToPoi("", "", "Error fetching distance and time");
                    e.printStackTrace();
                }
            }
        }).execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + currentLocation.latitude + "," + currentLocation.longitude + "&destination=" + poiLocation.latitude + "," + poiLocation.longitude + "&key=" + context.getResources().getString(R.string.api_key));

    }

    public void fetchAmenitiesForStop(Stop stop, final RouteManager.FetchAmenitiesForStopListener fetchAmenitiesForStopListener) {
        getRoutesService().fetchAmenitiesForStop(stop, new FetchAmenitiesForStopListener() {
            @Override
            public void didFinishFetchingAmenitiesForStop(Amenities amenities, String error) {
                fetchAmenitiesForStopListener.didFinishFetchingAmenitiesForStop(amenities, error);
            }
        });
    }
}
