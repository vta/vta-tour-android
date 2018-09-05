package com.vta.virtualtour.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vta.virtualtour.R;
import com.vta.virtualtour.models.MarkerInfo;
import com.vta.virtualtour.models.NearByCategory;

import java.util.List;

/**
 * Created by tushar
 * Created on 21-Jul-18.
 */

public class NearMeMeetupAdapter extends RecyclerView.Adapter<NearMeMeetupAdapter.MyViewHolder> {
    private List<MarkerInfo> list;
    private Context context;
    private NearByCategory nearByCategory;


    public NearMeMeetupAdapter(NearByCategory category, List<MarkerInfo> list, Context context) {
        this.list = list;
        this.context = context;
        this.nearByCategory = category;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.near_me_place_of_interest_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.placeOfInterestName.setText(list.get(position).getTitle());
        holder.bind(list.get(position).getUrl());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView placeOfInterestName;
        public ImageView iconView;

        public MyViewHolder(View view) {
            super(view);
            placeOfInterestName = (TextView) view.findViewById(R.id.name_textview);
            iconView = (ImageView) view.findViewById(R.id.name_icon);

        }

        public void bind(final String meetupURL) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(meetupURL));
                    context.startActivity(i);
                }
            });
        }
    }
}
