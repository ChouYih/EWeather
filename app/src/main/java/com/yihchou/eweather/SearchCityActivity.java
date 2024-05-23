package com.yihchou.eweather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.yihchou.eweather.WeatherFragment.*;

public class SearchCityActivity extends AppCompatActivity {
    private EditText editTextSearch;
    private Button buttonSearch;
    private Button buttonUseLocation;
    private ListView listViewCities;
    private CityAdapter cityAdapter;
    private List<LocationResponse.Location> cityList;
    private String nowLocation;
    private String cityName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        // 从 Intent 中获取当前位置信息和城市名称
        Intent intent = getIntent();
        nowLocation = intent.getStringExtra("nowLocation");
        cityName = intent.getStringExtra("cityName");

        editTextSearch = findViewById(R.id.editText_search);
        buttonSearch = findViewById(R.id.button_search);
        buttonUseLocation = findViewById(R.id.button_use_location);
        listViewCities = findViewById(R.id.listView_cities);

        cityAdapter = new CityAdapter(this, cityList);
        listViewCities.setAdapter(cityAdapter);

        buttonSearch.setOnClickListener(v -> searchCity(editTextSearch.getText().toString()));

        buttonUseLocation.setOnClickListener(v -> {
            getIpLocation();
        });

        listViewCities.setOnItemClickListener((parent, view, position, id) -> {
            LocationResponse.Location selectedCity = (LocationResponse.Location) parent.getItemAtPosition(position);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("nowLocation", selectedCity.id);
            resultIntent.putExtra("cityName", selectedCity.name);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void searchCity(String keyword) {
        GeoApi geoService = ApiClient.getClient(GEO_BASE_URL).create(GeoApi.class);
        Call<LocationResponse> call = geoService.getCityLocation(keyword, API_KEY);

        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cityList = response.body().location;
                    cityAdapter.updateData(cityList);
                } else {
                    Toast.makeText(SearchCityActivity.this, "搜索城市失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                Toast.makeText(SearchCityActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
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
                        searchLocationByAdcode(ipLocationResponse.adcode, ipLocationResponse.city);
                    } else {
                        runOnUiThread(() -> Toast.makeText(SearchCityActivity.this, "获取IP定位信息失败", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(SearchCityActivity.this, "获取IP定位信息失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<IpLocationResponse> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(SearchCityActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void searchLocationByAdcode(String adcode, String city) {
        GeoApi geoService = ApiClient.getClient(GEO_BASE_URL).create(GeoApi.class);
        Call<LocationResponse> call = geoService.getCityLocation(adcode, API_KEY);

        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LocationResponse.Location> locations = response.body().location;
                    if (locations != null && !locations.isEmpty()) {
                        String locationId = locations.get(0).id;
                        nowLocation = locationId;
                        cityName = city;
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("nowLocation", nowLocation);
                        resultIntent.putExtra("cityName", cityName);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(SearchCityActivity.this, "未找到相关城市", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(SearchCityActivity.this, "获取城市信息失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(SearchCityActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
