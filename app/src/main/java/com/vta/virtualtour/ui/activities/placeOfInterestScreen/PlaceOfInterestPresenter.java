package com.vta.virtualtour.ui.activities.placeOfInterestScreen;

import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.R;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.PlaceOfInterest;
import com.vta.virtualtour.models.PlaceOfInterestCategory;
import com.vta.virtualtour.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tushar on 13/04/18.
 */

public class PlaceOfInterestPresenter implements PlaceOfInterestContract.Presenter {
    private PlaceOfInterestContract.View view;

    public PlaceOfInterestPresenter(PlaceOfInterestContract.View view) {
        this.view = view;
    }

    @Override
    public void fetchCurrentLocation() {
        view.setupLocationManager();
    }

    @Override
    public void fetchPlaceOfInterestList(final LatLng currentLocation,boolean fromNearMe) {

        RouteManager.getSharedInstance().fetchCompletePoiDetails(currentLocation,fromNearMe ? Constants.NEARBY_PLACE_OF_INTEREST_RADIUS:Constants.PLACE_OF_INTEREST_RADIUS,new RouteManager.FetchCompletePoiDetailsListener() {
            @Override
            public void didFinishFetchingCompletePoiDetails(final JSONArray jsonArray, String error) {
                final ArrayList<PlaceOfInterest> placeOfInterestArrayList = new ArrayList<>();
                final ArrayList<PlaceOfInterestCategory> placeOfInterestTypeArrayList = new ArrayList<>();

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

                        ArrayList<String> typesArray = new ArrayList<String>();
                        JSONArray typesJsonArray = data.getJSONArray("types");
                        for (int j = 0; j < typesJsonArray.length(); j++){
                            typesArray.add(typesJsonArray.getString(j));
                            if (placeOfInterestTypeArrayList.size() == 0){
                                placeOfInterestTypeArrayList.add(new PlaceOfInterestCategory(reformatString(typesJsonArray.getString(j)), typesJsonArray.getString(j)));
                            }else {
                                boolean isValueMatching = false;
                                for (int k = 0; k < placeOfInterestTypeArrayList.size(); k++) {
                                    if (typesJsonArray.getString(j).equals(placeOfInterestTypeArrayList.get(k).getKey())) {
                                        isValueMatching = true;
                                    }
                                }
                                if (!isValueMatching){
                                    placeOfInterestTypeArrayList.add(new PlaceOfInterestCategory(reformatString(typesJsonArray.getString(j)), typesJsonArray.getString(j)));
                                }
                            }
                        }
                        placeOfInterestArrayList.add(new PlaceOfInterest(data.getString("place_id"), data.getString("name"), "", "", "", "", photoUrl, currentLocation, new LatLng(Double.parseDouble(jsonObjectLocation.getString("lat")), Double.parseDouble(jsonObjectLocation.getString("lng"))), rating, typesArray));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                view.showCategory(placeOfInterestTypeArrayList);
                view.reloadPlaceOfInterestList(placeOfInterestArrayList);

            }
        });
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

    private String reformatString(String name){
        String reformattedString = "";

        if (name.indexOf("_") == -1){
            reformattedString = makeFirstLetterCapital(name);
        }else {
            String[] newString = name.split("_");
            for (int i = 0; i < newString.length; i++){
                reformattedString = reformattedString + makeFirstLetterCapital(newString[i])+" ";
            }
        }

        return reformattedString;
    }

    private String makeFirstLetterCapital(String name){
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
