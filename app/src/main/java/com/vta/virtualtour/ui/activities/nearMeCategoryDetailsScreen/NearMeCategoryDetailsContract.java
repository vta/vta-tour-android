package com.vta.virtualtour.ui.activities.nearMeCategoryDetailsScreen;

import com.google.android.gms.maps.model.LatLng;
import com.vta.virtualtour.models.NearByCategory;
import com.vta.virtualtour.models.Stop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tushar on 20-07-2018.
 */

public interface NearMeCategoryDetailsContract {
    interface View {
        void reloadRecyclerView(List<String> list);
    }

    interface Presenter {
        void fetchAmenitiesForStop(Stop stop);
        void fetchConnectionsForStop(Stop stop);
        void fetchSocialGatheringForStop(Stop stop);
        void fetchPlaceOfInterestList(NearByCategory nearByCategory);
    }
}
