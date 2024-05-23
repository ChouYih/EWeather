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

public class DailyWeatherAdapter extends BaseAdapter {

    private Context context;
    private List<DailyWeatherResponse.Daily> dailyWeatherList;
    private SimpleDateFormat inputFormat;
    private SimpleDateFormat outputFormat;

    public DailyWeatherAdapter(Context context, List<DailyWeatherResponse.Daily> dailyWeatherList) {
        this.context = context;
        this.dailyWeatherList = dailyWeatherList;
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.outputFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return dailyWeatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return dailyWeatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_daily_weather, parent, false);
        }

        DailyWeatherResponse.Daily weatherData = dailyWeatherList.get(position);

        TextView date = convertView.findViewById(R.id.textView_date);
        ImageView weatherIcon = convertView.findViewById(R.id.imageView_item_weather_icon_7d);
        TextView weather = convertView.findViewById(R.id.textView_item_weather_7d);
        TextView temperature = convertView.findViewById(R.id.textView_item_temperature_7d);

        try {
            Date parsedDate = inputFormat.parse(weatherData.fxDate);
            date.setText(outputFormat.format(parsedDate));
        } catch (Exception e) {
            date.setText(weatherData.fxDate);
        }

        Glide.with(context)
                .load("file:///android_asset/weather_icons_2/" + weatherData.iconDay + ".png")
                .into(weatherIcon);
        weather.setText(weatherData.textDay);
        temperature.setText(weatherData.tempMin + "°C" + "\n" + weatherData.tempMax + "°C");

        return convertView;
    }
}
