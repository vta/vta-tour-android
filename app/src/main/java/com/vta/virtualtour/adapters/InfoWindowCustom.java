package com.vta.virtualtour.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.vta.virtualtour.R;

/**
 * Created by tushar
 * Created on 09-05-2018.
 */

public class InfoWindowCustom implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public InfoWindowCustom(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
//        LayoutInflater inflater = (LayoutInflater)
//                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflater.inflate(R.layout.stops_info_window_new, null);
//        ((TextView) v.findViewById(R.id.marker_title)).setText(marker.getTitle());
//        ((TextView) v.findViewById(R.id.marker_subtitle)).setText(marker.getSnippet());
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LinearLayout info = new LinearLayout(this.context);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this.context);
        title.setTextColor(Color.GRAY);
        title.setGravity(Gravity.LEFT);
        title.setTextSize(15);
        title.setText(marker.getTitle());

        TextView snippet = new TextView(this.context);
        snippet.setTextColor(Color.GRAY);
        snippet.setTextSize(13);
        snippet.setText(marker.getSnippet());

        info.addView(title);
        info.addView(snippet);

        return info;
    }
}
