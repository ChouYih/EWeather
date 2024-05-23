package com.yihchou.eweather;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {
    private static final String BASE_URL = "https://devapi.qweather.com";
    private static final String GEO_BASE_URL = "https://geoapi.qweather.com";
    private static final String API_KEY = "1bcd852c37b445ba931e37a46405e2ea";
    private static final String AMAP_BASE_URL = "https://restapi.amap.com";
    private static final String AMAP_API_KEY = "57ebc1c69c2ae413a3be870084e37481";

    // UI 元素
    private TextView textViewCity;
    private ImageView imageViewWeatherIcon;
    private TextView textViewWeather;
    private TextView textViewTemperature;
    private TextView textViewWeatherDescription;
    private ImageView buttonRefresh;
    private ImageView buttonChangeCity;
    private ListView listViewHourly;
    private ListView listViewDaily;
    private ScrollView scrollViewWeather;
    private ExecutorService executorService;

    private  String cityName = "NULL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        // 初始化UI元素
        textViewCity = view.findViewById(R.id.textView_city);
        imageViewWeatherIcon = view.findViewById(R.id.imageView_weather_icon);
        textViewWeather = view.findViewById(R.id.textView_weather);
        textViewTemperature = view.findViewById(R.id.textView_temperature);
        textViewWeatherDescription = view.findViewById(R.id.textView_weather_description);
        buttonRefresh = view.findViewById(R.id.button_refresh);
        buttonChangeCity = view.findViewById(R.id.button_change_city);
        listViewHourly = view.findViewById(R.id.listView_hourly);
        listViewDaily = view.findViewById(R.id.listView_daily);
        scrollViewWeather = view.findViewById(R.id.scrollView_weather);

        executorService = Executors.newFixedThreadPool(3);



        buttonRefresh.setOnClickListener(v -> refreshWeatherData("101081213")); // 示例 Location ID

        // 初始化天气数据
        getIpLocation();

        listViewHourly.setOnItemClickListener((parent, view1, position, id) -> {
            HourlyWeatherResponse.Hourly hourlyWeather = (HourlyWeatherResponse.Hourly) parent.getItemAtPosition(position);
            showHourlyWeatherDetail(hourlyWeather);
        });

        listViewDaily.setOnItemClickListener((parent, view12, position, id) -> {
            DailyWeatherResponse.Daily dailyWeather = (DailyWeatherResponse.Daily) parent.getItemAtPosition(position);
            showDailyWeatherDetail(dailyWeather);
        });

        return view;
    }


    private void getIpLocation() {
        AmapApi amapService = ApiClient.getClient(AMAP_BASE_URL).create(AmapApi.class);
        Call<IpLocationResponse> call = amapService.getIpLocation(AMAP_API_KEY);

        call.enqueue(new Callback<IpLocationResponse>() {
            @Override
            public void onResponse(Call<IpLocationResponse> call, Response<IpLocationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    IpLocationResponse ipLocationResponse = response.body();
                    if ("1".equals(ipLocationResponse.status)) {
                        searchLocationByAdcode(ipLocationResponse.adcode);
                        cityName = ipLocationResponse.city;
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "获取IP定位信息失败", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "获取IP定位信息失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<IpLocationResponse> call, Throwable t) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    Log.e("WeatherFragment", "Network error", t);
                });
            }
        });
    }

    private void searchLocationByAdcode(String adcode) {
        GeoApi geoService = ApiClient.getClient(GEO_BASE_URL).create(GeoApi.class);
        Call<LocationResponse> call = geoService.getCityLocation(adcode, API_KEY);

        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LocationResponse.Location> locations = response.body().location;
                    if (locations != null && !locations.isEmpty()) {
                        String locationId = locations.get(0).id;
                        refreshWeatherData(locationId);
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "未找到相关城市", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "获取城市信息失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    Log.e("WeatherFragment", "Network error", t);
                });
            }
        });
    }

    private void refreshWeatherData(String location) {
        executorService.execute(() -> getCurrentWeather(location));
        executorService.execute(() -> getHourlyWeather(location));
        executorService.execute(() -> getDailyWeather(location));
    }

    private void getCurrentWeather(String location) {
        WeatherApi apiService = ApiClient.getClient(BASE_URL).create(WeatherApi.class);

        Call<RealTimeWeatherResponse> call = apiService.getCurrentWeather(location, API_KEY);
        call.enqueue(new Callback<RealTimeWeatherResponse>() {
            @Override
            public void onResponse(Call<RealTimeWeatherResponse> call, Response<RealTimeWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RealTimeWeatherResponse weatherResponse = response.body();
                    if (weatherResponse.now != null) {
                        getActivity().runOnUiThread(() -> updateCurrentWeatherUI(weatherResponse));
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "天气数据为空", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "获取天气数据失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<RealTimeWeatherResponse> call, Throwable t) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    Log.e("WeatherFragment", "Network error", t);
                });
            }
        });
    }

    private void getHourlyWeather(String location) {
        WeatherApi apiService = ApiClient.getClient(BASE_URL).create(WeatherApi.class);

        Call<HourlyWeatherResponse> call = apiService.get24HourWeather(location, API_KEY);
        call.enqueue(new Callback<HourlyWeatherResponse>() {
            @Override
            public void onResponse(Call<HourlyWeatherResponse> call, Response<HourlyWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HourlyWeatherResponse hourlyWeatherResponse = response.body();
                    if (hourlyWeatherResponse.hourly != null) {
                        getActivity().runOnUiThread(() -> updateHourlyWeatherUI(hourlyWeatherResponse.hourly.subList(0, 6))); // 获取未来6小时天气数据
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "逐小时天气数据为空", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "获取逐小时天气数据失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<HourlyWeatherResponse> call, Throwable t) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    Log.e("WeatherFragment", "Network error", t);
                });
            }
        });
    }

    private void getDailyWeather(String location) {
        WeatherApi apiService = ApiClient.getClient(BASE_URL).create(WeatherApi.class);

        Call<DailyWeatherResponse> call = apiService.get7DayWeather(location, API_KEY);
        call.enqueue(new Callback<DailyWeatherResponse>() {
            @Override
            public void onResponse(Call<DailyWeatherResponse> call, Response<DailyWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DailyWeatherResponse dailyWeatherResponse = response.body();
                    if (dailyWeatherResponse.daily != null) {
                        getActivity().runOnUiThread(() -> updateDailyWeatherUI(dailyWeatherResponse.daily));
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "未来7天天气数据为空", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "获取未来7天天气数据失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<DailyWeatherResponse> call, Throwable t) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    Log.e("WeatherFragment", "Network error", t);
                });
            }
        });
    }

    private void updateCurrentWeatherUI(RealTimeWeatherResponse weatherResponse) {
        RealTimeWeatherResponse.Now now = weatherResponse.now;

        // 更新UI组件
        textViewCity.setText(cityName);
        textViewWeather.setText(now.weatherDescription);
        textViewTemperature.setText(now.temperature + "°C");
        textViewWeatherDescription.setText(now.weatherDescription);

        // 使用 Glide 加载assets下的天气图标，确保路径正确
        String iconName = "file:///android_asset/weather_icons_2/" + now.icon + ".png";
        Glide.with(this)
                .load(iconName)
                .into(imageViewWeatherIcon);
    }

    private void updateHourlyWeatherUI(List<HourlyWeatherResponse.Hourly> hourlyWeatherList) {
        HourlyWeatherAdapter adapter = new HourlyWeatherAdapter(getContext(), hourlyWeatherList);
        listViewHourly.setAdapter(adapter);
    }

    private void updateDailyWeatherUI(List<DailyWeatherResponse.Daily> dailyWeatherList) {
        DailyWeatherAdapter adapter = new DailyWeatherAdapter(getContext(), dailyWeatherList);
        listViewDaily.setAdapter(adapter);
    }

    private void showHourlyWeatherDetail(HourlyWeatherResponse.Hourly hourlyWeather) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_weather_detail, null);
        builder.setView(dialogView);

        ImageView weatherIcon = dialogView.findViewById(R.id.imageView_weather_icon);
        TextView weatherDetails = dialogView.findViewById(R.id.textView_weather_details);

        String iconName = "file:///android_asset/weather_icons_2/" + hourlyWeather.icon + ".png";
        Glide.with(getContext())
                .load(iconName)
                .into(weatherIcon);

        String message = "Time: " + hourlyWeather.fxTime + "\n" +
                "Temperature: " + hourlyWeather.temp + "°C\n" +
                "Description: " + hourlyWeather.text + "\n" +
                "Wind: " + hourlyWeather.windDir + " " + hourlyWeather.windSpeed + " km/h\n" +
                "Humidity: " + hourlyWeather.humidity + "%\n" +
                "Pressure: " + hourlyWeather.pressure + " hPa";

        weatherDetails.setText(message);

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDailyWeatherDetail(DailyWeatherResponse.Daily dailyWeather) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_weather_detail, null);
        builder.setView(dialogView);

        ImageView weatherIcon = dialogView.findViewById(R.id.imageView_weather_icon);
        TextView weatherDetails = dialogView.findViewById(R.id.textView_weather_details);

        String iconName = "file:///android_asset/weather_icons_2/" + dailyWeather.iconDay + ".png";
        Glide.with(getContext())
                .load(iconName)
                .into(weatherIcon);

        String message = "Date: " + dailyWeather.fxDate + "\n" +
                "Max Temperature: " + dailyWeather.tempMax + "°C\n" +
                "Min Temperature: " + dailyWeather.tempMin + "°C\n" +
                "Daytime: " + dailyWeather.textDay + "\n" +
                "Nighttime: " + dailyWeather.textNight + "\n" +
                "Wind: " + dailyWeather.windDirDay + " " + dailyWeather.windSpeedDay + " km/h\n" +
                "Humidity: " + dailyWeather.humidity + "%\n" +
                "Pressure: " + dailyWeather.pressure + " hPa";

        weatherDetails.setText(message);

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}

