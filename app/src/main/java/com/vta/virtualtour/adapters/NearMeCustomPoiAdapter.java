package com.vta.virtualtour.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vta.virtualtour.R;
import com.vta.virtualtour.models.MarkerInfo;
import com.vta.virtualtour.utility.Utils;

import java.util.List;


/**
 * Created by sidhesh.naik on 30/07/18.
 */

public class NearMeCustomPoiAdapter extends RecyclerView.Adapter<NearMeCustomPoiAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MarkerInfo markerInfo);
    }

    private final NearMeCustomPoiAdapter.OnItemClickListener listener;

    private List<MarkerInfo> markerInfoArrayList;

    public NearMeCustomPoiAdapter(List<MarkerInfo> markerInfoArrayList, NearMeCustomPoiAdapter.OnItemClickListener listener) {
        this.markerInfoArrayList = markerInfoArrayList;
        this.listener = listener;
    }

    @Override
    public NearMeCustomPoiAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.near_me_custom_poi_list_row, parent, false);

        return new NearMeCustomPoiAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NearMeCustomPoiAdapter.MyViewHolder holder, int position) {
        MarkerInfo markerInfo = markerInfoArrayList.get(position);
        holder.name.setText(markerInfo.getTitle());
        holder.address.setText(markerInfo.getSubtitle());

        if (!markerInfo.getIcon().isEmpty()) {
            Bitmap markerBitmap = Utils.getBase64Bitmap(markerInfo.getIcon());
            holder.icon.setImageBitmap(Bitmap.createBitmap(markerBitmap));
        }
        holder.bind(markerInfo, listener);
    }

    @Override
    public int getItemCount() {
        return markerInfoArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView address;
        public ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_textview);
            address = view.findViewById(R.id.address_textview);
            icon =  view.findViewById(R.id.custom_poi_imageview);
        }

        public void bind(final MarkerInfo markerInfo, final NearMeCustomPoiAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(markerInfo);
                }
            });
        }
    }
}
