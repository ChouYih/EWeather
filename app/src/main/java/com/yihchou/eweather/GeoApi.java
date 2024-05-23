package com.yihchou.eweather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface GeoApi {
    @GET("/v2/city/lookup")
    Call<LocationResponse> getCityLocation(
            @Query("location") String location,
            @Query("key") String apiKey
    );
}
