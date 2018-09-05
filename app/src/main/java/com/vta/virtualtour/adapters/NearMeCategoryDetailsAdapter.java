package com.vta.virtualtour.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vta.virtualtour.R;
import com.vta.virtualtour.models.NearByCategory;

import java.util.List;

/**
 * Created by tushar
 * Created on 21-Jul-18.
 */

public class NearMeCategoryDetailsAdapter extends RecyclerView.Adapter<NearMeCategoryDetailsAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String name);
    }

    private List<String> list;
    private NearByCategory nearByCategory;
    private final NearMeCategoryDetailsAdapter.OnItemClickListener listener;


    public NearMeCategoryDetailsAdapter(NearByCategory category, List<String> list, OnItemClickListener listener) {
        this.list = list;
        this.nearByCategory = category;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.near_me_place_of_interest_list_row, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.placeOfInterestName.setText(list.get(position));

        if (!nearByCategory.getKey().equals("social_gathering")) {
            holder.iconView.setVisibility(View.GONE);
        }

        if (nearByCategory.getKey().equals("connections")) {
            holder.bind(list.get(position), listener);
        }
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

        public void bind(final String name, final NearMeCategoryDetailsAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(name);
                }
            });
        }
    }
}
