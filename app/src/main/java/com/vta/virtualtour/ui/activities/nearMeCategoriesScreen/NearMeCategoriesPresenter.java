package com.vta.virtualtour.ui.activities.nearMeCategoriesScreen;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.NearByCategory;
import com.vta.virtualtour.models.NearByPlaceOfInterest;
import com.vta.virtualtour.models.PlaceOfInterest;
import com.vta.virtualtour.models.PlaceOfInterestCategory;
import com.vta.virtualtour.models.Route;
import com.vta.virtualtour.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tushar on 7/19/2018.
 */
public class NearMeCategoriesPresenter implements NearMeCategoriesContract.Presenter {

    private NearMeCategoriesContract.View view;

    public NearMeCategoriesPresenter(NearMeCategoriesContract.View view) {
        this.view = view;
    }

    @Override
    public void fetchPlaceOfInterestList(final LatLng currentLocation) {
        RouteManager.getSharedInstance().fetchCompletePoiDetails(currentLocation, Constants.NEARBY_PLACE_OF_INTEREST_RADIUS,new RouteManager.FetchCompletePoiDetailsListener() {
            @Override
            public void didFinishFetchingCompletePoiDetails(final JSONArray jsonArray, String error) {
                final ArrayList<NearByCategory> nearByCategoryArrayList = new ArrayList<>();
                final ArrayList<NearByPlaceOfInterest> nearByPlaceOfInterestArrayList = new ArrayList<>();

                //hardcoding 3 categories
                nearByCategoryArrayList.add(new NearByCategory("amenity", "Amenities", false));
                nearByCategoryArrayList.add(new NearByCategory("connections", "Connections", false));
                nearByCategoryArrayList.add(new NearByCategory("social_gathering", "Social Gathering", false));
                nearByCategoryArrayList.add(new NearByCategory("custom_poi", "Custom POI", false));

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        final JSONObject data = jsonArray.getJSONObject(i);
                        JSONArray typesJsonArray = data.getJSONArray("types");
                        for (int j = 0; j < typesJsonArray.length(); j++) {
                            if (nearByCategoryArrayList.size() == 0){
                                nearByCategoryArrayList.add(new NearByCategory(typesJsonArray.getString(j), reformatString(typesJsonArray.getString(j)),true));
                            }else {
                                boolean isValueMatching = false;
                                for (int k = 0; k < nearByCategoryArrayList.size(); k++) {
                                    if (typesJsonArray.getString(j).equals(nearByCategoryArrayList.get(k).getKey())) {
                                        isValueMatching = true;
                                    }
                                }
                                if (!isValueMatching){
                                    nearByCategoryArrayList.add(new NearByCategory(typesJsonArray.getString(j), reformatString(typesJsonArray.getString(j)), true));
                                }
                            }
                            nearByPlaceOfInterestArrayList.add(new NearByPlaceOfInterest(data.getString("name"), typesJsonArray.getString(j)));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                RouteManager.getSharedInstance().setNearByPlaceOfInterests(nearByPlaceOfInterestArrayList);
                view.showCategory(nearByCategoryArrayList);

            }
        });
    }

    @Override
    public void fetchIntegrations() {
        RouteManager.getSharedInstance().fetchIntegrations();
    }

    @Override
    public void showProgressBar() {
        view.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        view.hideProgressBar();
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
