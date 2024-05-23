package com.yihchou.eweather;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DailyWeatherResponse {
    @SerializedName("code")
    public String code;

    @SerializedName("updateTime")
    public String updateTime;

    @SerializedName("daily")
    public List<Daily> daily;

    public class Daily {
        @SerializedName("fxDate")
        public String fxDate;

        @SerializedName("sunrise")
        public String sunrise;

        @SerializedName("sunset")
        public String sunset;

        @SerializedName("moonrise")
        public String moonrise;

        @SerializedName("moonset")
        public String moonset;

        @SerializedName("moonPhase")
        public String moonPhase;

        @SerializedName("moonPhaseIcon")
        public String moonPhaseIcon;

        @SerializedName("tempMax")
        public String tempMax;

        @SerializedName("tempMin")
        public String tempMin;

        @SerializedName("iconDay")
        public String iconDay;

        @SerializedName("textDay")
        public String textDay;

        @SerializedName("iconNight")
        public String iconNight;

        @SerializedName("textNight")
        public String textNight;

        @SerializedName("wind360Day")
        public String wind360Day;

        @SerializedName("windDirDay")
        public String windDirDay;

        @SerializedName("windScaleDay")
        public String windScaleDay;

        @SerializedName("windSpeedDay")
        public String windSpeedDay;

        @SerializedName("wind360Night")
        public String wind360Night;

        @SerializedName("windDirNight")
        public String windDirNight;

        @SerializedName("windScaleNight")
        public String windScaleNight;

        @SerializedName("windSpeedNight")
        public String windSpeedNight;

        @SerializedName("humidity")
        public String humidity;

        @SerializedName("precip")
        public String precip;

        @SerializedName("pressure")
        public String pressure;

        @SerializedName("vis")
        public String vis;

        @SerializedName("cloud")
        public String cloud;

        @SerializedName("uvIndex")
        public String uvIndex;
    }

    @SerializedName("refer")
    public Refer refer;

    public class Refer {
        @SerializedName("sources")
        public List<String> sources;

        @SerializedName("license")
        public List<String> license;
    }
}
