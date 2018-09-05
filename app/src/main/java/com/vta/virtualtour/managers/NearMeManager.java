package com.vta.virtualtour.managers;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.R;
import com.vta.virtualtour.VirtualTourApplication;
import com.vta.virtualtour.interfaces.AsyncTaskJsonResultListener;
import com.vta.virtualtour.utility.JSONAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by tushar
 * Created on 21/08/18.
 */

public class NearMeManager {


    public interface POICheckListener {
        void didFinishPOICheck(String poiType, String error);
    }

    private static NearMeManager manager;

    public static NearMeManager getSharedInstance() {
        if (manager == null) {
            manager = new NearMeManager();
        }
        return manager;
    }


    public void checkPOIExistsForPOIType(LatLng location, final String poiType, final NearMeManager.POICheckListener poiCheckListener) {

        new JSONAsyncTask(new AsyncTaskJsonResultListener() {
            @Override
            public void asyncTaskResult(JSONObject jsonObject) {
                // Mapping
                String selectedPoiType = "";
                JSONArray resultsArray = null;
                String error = "";
                try {


                    if (jsonObject != null) {
                        resultsArray = jsonObject.getJSONArray("results");

                        if (resultsArray.length() > 0) {
                            selectedPoiType = poiType;
                        } else {
                            error = "error";

                        }
                    } else {
                        error = "error";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error = "error";
                }

                poiCheckListener.didFinishPOICheck(selectedPoiType, error);
            }
        }).execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location.latitude + "," + location.longitude + "&radius=" + 500 + "&type=" + poiType + "&key=" + VirtualTourApplication.getAppContext().getResources().getString(R.string.api_key));
    }
}
