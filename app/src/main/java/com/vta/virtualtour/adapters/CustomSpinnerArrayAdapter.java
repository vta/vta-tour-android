package com.vta.virtualtour.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vta.virtualtour.R;
import com.vta.virtualtour.models.PlaceOfInterestCategory;

import java.util.ArrayList;

/**
 * Created by tushar on 23/04/18.
 */

public class CustomSpinnerArrayAdapter extends ArrayAdapter<PlaceOfInterestCategory> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final ArrayList<PlaceOfInterestCategory> placeOfInterestCategoryArrayList;

    public CustomSpinnerArrayAdapter(@NonNull Context context,
                                     @NonNull ArrayList<PlaceOfInterestCategory> placeOfInterestCategoryArrayList) {
        super(context,0,placeOfInterestCategoryArrayList);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.placeOfInterestCategoryArrayList = placeOfInterestCategoryArrayList;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public
    @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.spinner_item,parent, false);
        }
        TextView txtTitle = (TextView) convertView.findViewById(R.id.name);
        txtTitle.setText(placeOfInterestCategoryArrayList.get(position).getName());

        return convertView;
    }
}