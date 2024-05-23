package com.yihchou.eweather;

import com.google.gson.annotations.SerializedName;

public class RealTimeWeatherResponse {
    @SerializedName("code")
    public String code;

    @SerializedName("updateTime")
    public String updateTime;

    @SerializedName("now")
    public Now now;

    public class Now {
        @SerializedName("obsTime")
        public String obsTime;

        @SerializedName("temp")
        public String temperature;

        @SerializedName("feelsLike")
        public String feelsLike;

        @SerializedName("icon")
        public String icon;

        @SerializedName("text")
        public String weatherDescription;

        @SerializedName("windDir")
        public String windDirection;

        @SerializedName("windScale")
        public String windScale;

        @SerializedName("humidity")
        public String humidity;

        @SerializedName("precip")
        public String precipitation;

        @SerializedName("pressure")
        public String pressure;

        @SerializedName("vis")
        public String visibility;

        @SerializedName("cloud")
        public String cloud;

        @SerializedName("dew")
        public String dewPoint;
    }
}
