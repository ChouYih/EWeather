package com.yihchou.eweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HourlyWeatherAdapter extends BaseAdapter {

    private Context context;
    private List<HourlyWeatherResponse.Hourly> hourlyWeatherList;
    private SimpleDateFormat inputFormat;
    private SimpleDateFormat outputFormat;

    public HourlyWeatherAdapter(Context context, List<HourlyWeatherResponse.Hourly> hourlyWeatherList) {
        this.context = context;
        this.hourlyWeatherList = hourlyWeatherList;
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX", Locale.getDefault());
        this.outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return hourlyWeatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return hourlyWeatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_hourly_weather, parent, false);
        }

        HourlyWeatherResponse.Hourly weatherData = hourlyWeatherList.get(position);

        TextView time = convertView.findViewById(R.id.textView_time);
        ImageView weatherIcon = convertView.findViewById(R.id.imageView_item_weather_icon);
        TextView weather = convertView.findViewById(R.id.textView_item_weather);
        TextView temperature = convertView.findViewById(R.id.textView_item_temperature);

        try {
            Date date = inputFormat.parse(weatherData.fxTime);
            time.setText(outputFormat.format(date));
        } catch (Exception e) {
            time.setText(weatherData.fxTime);
        }

        Glide.with(context)
                .load("file:///android_asset/weather_icons_2/" + weatherData.icon + ".png")
                .into(weatherIcon);
        weather.setText(weatherData.text);
        temperature.setText(weatherData.temp + "°C");

        return convertView;
    }
}
