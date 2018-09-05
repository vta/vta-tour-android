package com.vta.virtualtour.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vta.virtualtour.R;
import com.vta.virtualtour.models.PlaceOfInterest;
import com.vta.virtualtour.utility.RoundedTransformation;

import java.util.ArrayList;


/**
 * Created by tushar on 13/04/18.
 */

public class PlaceOfInterestListAdapter extends RecyclerView.Adapter<PlaceOfInterestListAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PlaceOfInterest placeOfInterest);
    }

    private final OnItemClickListener listener;

    private ArrayList<PlaceOfInterest> placeOfInterestArrayList;
    private Context context;

    public PlaceOfInterestListAdapter(Context applicationContext, ArrayList<PlaceOfInterest> placeOfInterestArrayList, OnItemClickListener listener) {
        this.placeOfInterestArrayList = placeOfInterestArrayList;
        this.listener = listener;
        this.context = applicationContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_of_interest_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PlaceOfInterest placeOfInterest = placeOfInterestArrayList.get(position);
        if (placeOfInterest.getRatings() == 0) {
            holder.ratingBar.setVisibility(View.INVISIBLE);
        } else {
            holder.ratingBar.setVisibility(View.VISIBLE);
        }

        holder.placeOfInterestName.setText(placeOfInterest.getName());
        holder.ratingBar.setRating(placeOfInterest.getRatings());
        if (placeOfInterest.getImageUrl() != null && !placeOfInterest.getImageUrl().isEmpty()) {

            Picasso.get()
                    .load(placeOfInterest.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .transform(new RoundedTransformation(10, 2, context))
                    .resizeDimen(R.dimen.poi_image_size, R.dimen.poi_image_size)
                    .centerCrop()
                    .into(holder.placeOfInterestImage);
        } else {
            holder.placeOfInterestImage.setImageResource(R.mipmap.ic_launcher);
        }
        holder.bind(placeOfInterest, listener);
    }

    @Override
    public int getItemCount() {
        return placeOfInterestArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView placeOfInterestName;
        public RatingBar ratingBar;
        public ImageView placeOfInterestImage;

        public MyViewHolder(View view) {
            super(view);
            placeOfInterestName = (TextView) view.findViewById(R.id.name_textview);
            ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
            placeOfInterestImage = (ImageView) view.findViewById(R.id.place_of_interest_imageview);
        }

        public void bind(final PlaceOfInterest placeOfInterest, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(placeOfInterest);
                }
            });
        }
    }
}
