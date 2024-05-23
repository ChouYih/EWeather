package com.yihchou.eweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CityAdapter extends BaseAdapter {
    private Context context;
    private List<LocationResponse.Location> cityList;

    public CityAdapter(Context context, List<LocationResponse.Location> cityList) {
        this.context = context;
        this.cityList = cityList;
    }

    @Override
    public int getCount() {
        return cityList == null ? 0 : cityList.size();
    }

    @Override
    public Object getItem(int position) {
        return cityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_city, parent, false);
        }

        TextView textViewCityName = convertView.findViewById(R.id.textView_city_name);
        textViewCityName.setText(cityList.get(position).name);

        return convertView;
    }

    public void updateData(List<LocationResponse.Location> cityList) {
        this.cityList = cityList;
        notifyDataSetChanged();
    }
}
