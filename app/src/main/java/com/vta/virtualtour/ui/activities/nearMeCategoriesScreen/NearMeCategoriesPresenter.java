package com.vta.virtualtour.ui.activities.nearMeCategoriesScreen;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.managers.NearMeManager;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.NearByCategory;
import com.vta.virtualtour.models.NearByPlaceOfInterest;

import java.util.ArrayList;

/**
 * Created by tushar
 * Created on 7/19/2018.
 */
public class NearMeCategoriesPresenter implements NearMeCategoriesContract.Presenter {

    private NearMeCategoriesContract.View view;


    private ArrayList<NearByCategory> nearByCategoryArrayList = new ArrayList<>();
    private ArrayList<NearByPlaceOfInterest> nearByPlaceOfInterestArrayList = new ArrayList<>();

    private boolean loadMore = false;
    private int primaryPOICount = 0;
    private static final String[] arrPOI_Type = {"transit_station",
            "airport",
            "gym|stadium|bowling_alley",
            "school|library",
            "restaurant|cafe|bakery|bar",
            "supermarket|shopping_mall",
            "store",
            "post_office",
            "museum|art_gallery|aquarium|zoo",
            "courthouse|local_government_office|City_hall",
            "doctor|dentist",
            "hospital|pharmacy|veterinary_care",
            "park|amusement_park",
            "church|hindu_temple|synagogue|mosque",
            "movie_theater|night_club",
            "hair_care|spa|beauty_salon",
            "physiotherapist",
            "parking|lodging",
            "police|fire_station",
            "atm|bank",
            "cemetery",
            "accounting",
            "insurance_agency",
            "real_estate_agency",
            "lawyer",
            "laundry",
            "meal_delivery|meal_takeaway",
            "movie_rental",
            "florist",
            "travel_agency",
            "funeral_home",
            "electrician",
            "locksmith",
            "painter",
            "plumber",
            "roofing_contractor",
            "taxi_stand",
            "gas_station",
            "moving_company",
            "storage",
            "casino",
            "rv_park",
            "car_wash",
            "car_repair",
            "car_rental",
            "car_dealer",
            "campground",
            "subway_station",
            "pet_store",
            "hardware_store",
            "department_store",
            "convenience_store",
            "bicycle_store",
            "book_store",
            "electronics_store",
            "clothing_store",
            "furniture_store",
            "shoe_store",
            "home_goods_store",
            "jewelry_store",
            "liquor_store",
            "furniture_store",
            "embassy"};

    public NearMeCategoriesPresenter(NearMeCategoriesContract.View view) {
        this.view = view;
    }

    @Override
    public void fetchPlaceOfInterestList(final LatLng currentLocation) {

        loadMore = false;
        primaryPOICount = 0;
        nearByCategoryArrayList = new ArrayList<>();
        nearByPlaceOfInterestArrayList = new ArrayList<>();

        nearByCategoryArrayList.add(new NearByCategory("amenity", "Amenities", false));
        nearByCategoryArrayList.add(new NearByCategory("social_gathering", "Social Gathering", false));
        nearByCategoryArrayList.add(new NearByCategory("connections", "Transit Connections", false));
        appendPOIS(currentLocation);
    }

    private void appendPOIS(final LatLng currentLocation) {

        if (primaryPOICount < (loadMore ? arrPOI_Type.length - 10 : 10)) {

            String poiType = arrPOI_Type[primaryPOICount];

            NearMeManager.getSharedInstance().checkPOIExistsForPOIType(currentLocation, poiType, new NearMeManager.POICheckListener() {
                @Override
                public void didFinishPOICheck(String poiType, String error) {

                    if (error.length() == 0) {
                        nearByCategoryArrayList.add(new NearByCategory(poiType, reformatString(poiType), true));
                        nearByPlaceOfInterestArrayList.add(new NearByPlaceOfInterest(poiType, poiType));
                    }

                    primaryPOICount++;
                    appendPOIS(currentLocation);
                }
            });

        } else {

            if (!loadMore) {
                nearByCategoryArrayList.add(new NearByCategory("custom_poi", "Custom POI", false));
                nearByCategoryArrayList.add(new NearByCategory("Load More...", "Load More...", false));
            }
            else {
                nearByCategoryArrayList.add(new NearByCategory("custom_poi", "Custom POI", false));
            }

            RouteManager.getSharedInstance().setNearByPlaceOfInterests(nearByPlaceOfInterestArrayList);
            view.showCategory(nearByCategoryArrayList);
        }
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

    @Override
    public void loadMorePOIS(LatLng latLng) {
        loadMore = true;
        primaryPOICount = 11;
        nearByCategoryArrayList = new ArrayList<>();
        nearByPlaceOfInterestArrayList = new ArrayList<>();

        appendPOIS(latLng);
    }

    private String reformatString(String name) {
        String reformattedString = "";

        if (!name.contains("_")) {
            reformattedString = makeFirstLetterCapital(name);
        } else {
            String[] newString = name.split("_");
            for (String aNewString : newString) {
                reformattedString = reformattedString + makeFirstLetterCapital(aNewString) + " ";
            }
        }

        String finalString = "";

        if (reformattedString.contains("|")) {
            String[] newString = reformattedString.split("\\|");
            for (int i = 0; i < newString.length - 1; i++) {
                finalString = finalString + makeFirstLetterCapital(newString[i]) + " | ";

            }
            finalString = finalString + makeFirstLetterCapital(newString[newString.length - 1]);
        } else {
            finalString = reformattedString;
        }

        return finalString;
    }

    private String makeFirstLetterCapital(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
