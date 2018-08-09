package com.vta.virtualtour.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vta.virtualtour.R;
import com.vta.virtualtour.models.CommonSpinnerDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tushar on 14/04/18.
 */

public class CustomSpinnerAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<CommonSpinnerDataModel> commonSpinnerDataModelArrayList;

    public CustomSpinnerAdapter(Context applicationContext, List<CommonSpinnerDataModel> commonSpinnerDataModelArrayList) {
        this.context = applicationContext;
        this.commonSpinnerDataModelArrayList = commonSpinnerDataModelArrayList;
        inflater = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return commonSpinnerDataModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View myView = null;
        try {
            Holder holder;
            myView = convertView;

            if (myView == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                myView = inflater.inflate(R.layout.custom_spinner_items, null);

                holder = new Holder();
                holder.tvName = (TextView) myView.findViewById(R.id.name_textview);
                myView.setTag(holder);
            } else {
                holder = (Holder) myView.getTag();
            }

            holder.tvName.setText(commonSpinnerDataModelArrayList.get(position).getName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return myView;
    }

    class Holder{
        private TextView tvName;
    }
}
