package com.yihchou.eweather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AmapApi {
    @GET("/v3/ip")
    Call<IpLocationResponse> getIpLocation(
            @Query("key") String apiKey
    );
}
