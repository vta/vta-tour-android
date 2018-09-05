package com.vta.virtualtour.ui.activities.placeOfInterestScreen;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.R;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.PlaceOfInterest;
import com.vta.virtualtour.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tushar
 * Created on 13/04/18.
 */

public class PlaceOfInterestPresenter implements PlaceOfInterestContract.Presenter {

    private PlaceOfInterestContract.View view;

    PlaceOfInterestPresenter(PlaceOfInterestContract.View view) {
        this.view = view;
    }

    @Override
    public void fetchPlaceOfInterestList(final LatLng currentLocation, String poiType, String token) {

        if (token == null) {

            RouteManager.getSharedInstance().fetchCompletePoiDetails(currentLocation, Constants.PLACE_OF_INTEREST_RADIUS, poiType, new RouteManager.FetchCompletePoiDetailsListener() {
                @Override
                public void didFinishFetchingCompletePoiDetails(JSONArray jsonArray, String error) {
                }

                @Override
                public void didFinishFetchingCompletePoiDetails(JSONArray jsonArray, String nextPageToken, String error) {

                    handlePOIFetchResponse(jsonArray, nextPageToken, currentLocation);
                }
            });
        } else {

            RouteManager.getSharedInstance().fetchCompletePoiDetails(token, new RouteManager.FetchCompletePoiDetailsListener() {
                @Override
                public void didFinishFetchingCompletePoiDetails(JSONArray jsonArray, String error) {
                }

                @Override
                public void didFinishFetchingCompletePoiDetails(JSONArray jsonArray, String nextPageToken, String error) {

                    handlePOIFetchResponse(jsonArray, nextPageToken, currentLocation);
                }
            });
        }
    }

    private void handlePOIFetchResponse(JSONArray jsonArray, String nextPageToken, LatLng currentLocation) {
        final ArrayList<PlaceOfInterest> placeOfInterests = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                final JSONObject data = jsonArray.getJSONObject(i);
                String photoUrl = "";
                float rating = 0;
                if (data.has("photos")) {
                    photoUrl = generatePhotoUrl(data.getJSONArray("photos"));
                }
                if (data.has("rating")) {
                    rating = Float.parseFloat(data.getString("rating"));
                }
                JSONObject jsonObjectGeometry = data.getJSONObject("geometry");
                JSONObject jsonObjectLocation = jsonObjectGeometry.getJSONObject("location");
                placeOfInterests.add(new PlaceOfInterest(data.getString("place_id"), data.getString("name"), "", "", "", "", photoUrl, currentLocation, new LatLng(Double.parseDouble(jsonObjectLocation.getString("lat")), Double.parseDouble(jsonObjectLocation.getString("lng"))), rating, new ArrayList<String>()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        view.reloadPlaceOfInterestList(placeOfInterests, nextPageToken);
    }


    private String generatePhotoUrl(JSONArray photos) {
        String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?";
        if (photos.length() > 0) {
            try {
                JSONObject jsonObject = photos.getJSONObject(0);
                photoUrl = photoUrl + "maxwidth=" + jsonObject.getString("width") + "&maxheight=" + jsonObject.getString("height") + "&photoreference=" + jsonObject.getString("photo_reference") + "&key=" + view.getContext().getResources().getString(R.string.api_key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return photoUrl;
    }
}
