package com.vta.virtualtour.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vta.virtualtour.R;
import com.vta.virtualtour.models.NearByCategory;
import com.vta.virtualtour.models.PlaceOfInterest;
import com.vta.virtualtour.models.PlaceOfInterestCategory;

import java.util.ArrayList;

/**
 * Created by tushar on 7/19/2018.
 */
public class NearMeCategoriesAdapter extends RecyclerView.Adapter<NearMeCategoriesAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(NearByCategory nearByCategory);
    }

    private final OnItemClickListener listener;

    private ArrayList<NearByCategory> nearByCategories;
    private Context context;

    public NearMeCategoriesAdapter(Context applicationContext, ArrayList<NearByCategory> nearByCategories, OnItemClickListener listener) {
        this.nearByCategories = nearByCategories;
        this.listener = listener;
        this.context = applicationContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.near_me_categories_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NearByCategory nearByCategory = nearByCategories.get(position);
        holder.placeOfInterestCategoryName.setText(nearByCategory.getName());
        holder.bind(nearByCategory, listener);
    }

    @Override
    public int getItemCount() {
        return nearByCategories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView placeOfInterestCategoryName;

        public MyViewHolder(View view) {
            super(view);
            placeOfInterestCategoryName = (TextView) view.findViewById(R.id.name_textview);
        }

        public void bind(final NearByCategory nearByCategory, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(nearByCategory);
                }
            });
        }
    }
}
