package com.yihchou.eweather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("v7/weather/now")
    Call<RealTimeWeatherResponse> getCurrentWeather(
            @Query("location") String location,
            @Query("key") String apiKey
    );

    @GET("v7/weather/24h")
    Call<HourlyWeatherResponse> get24HourWeather(
            @Query("location") String location,
            @Query("key") String apiKey
    );

    @GET("v7/weather/7d")
    Call<DailyWeatherResponse> get7DayWeather(
            @Query("location") String location,
            @Query("key") String apiKey
    );
}
