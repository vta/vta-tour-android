package com.vta.virtualtour.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vta.virtualtour.R;

import java.util.List;

/**
 * Created by tushar on 21-Jul-18.
 */

public class NearMeCategoryDetailsAdapter extends RecyclerView.Adapter<NearMeCategoryDetailsAdapter.MyViewHolder> {
    private List<String> list;
    private Context context;

    public NearMeCategoryDetailsAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView placeOfInterestName;
        public MyViewHolder(View view) {
            super(view);
            placeOfInterestName = (TextView) view.findViewById(R.id.name_textview);
        }
    }
}
