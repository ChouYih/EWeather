package com.yihchou.eweather;

import java.util.List;

public class LocationResponse {
    public String code;
    public List<Location> location;

    public static class Location {
        public String name;
        public String id;
        public String lat;
        public String lon;
        public String adm2;
        public String adm1;
        public String country;
        public String tz;
        public String utcOffset;
        public String isDst;
        public String type;
        public String rank;
        public String fxLink;
    }
}

