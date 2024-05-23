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

import static com.yihchou.eweather.WeatherFragment.API_KEY;
import static com.yihchou.eweather.WeatherFragment.GEO_BASE_URL;

public class SearchCityActivity extends AppCompatActivity {
    private EditText editTextSearch;
    private Button buttonSearch;
    private ListView listViewCities;
    private CityAdapter cityAdapter;
    private List<LocationResponse.Location> cityList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        editTextSearch = findViewById(R.id.editText_search);
        buttonSearch = findViewById(R.id.button_search);
        listViewCities = findViewById(R.id.listView_cities);

        cityAdapter = new CityAdapter(this, cityList);
        listViewCities.setAdapter(cityAdapter);

        buttonSearch.setOnClickListener(v -> searchCity(editTextSearch.getText().toString()));

        listViewCities.setOnItemClickListener((parent, view, position, id) -> {
            LocationResponse.Location selectedCity = (LocationResponse.Location) parent.getItemAtPosition(position);
            Intent intent = new Intent();
            intent.putExtra("cityId", selectedCity.id);
            intent.putExtra("cityName", selectedCity.name);
            setResult(RESULT_OK, intent);
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
}
