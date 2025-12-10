package com.example.mobileapp.network;

public class WeatherResponse {
    public Location location;
    public Current current;

    public static class Location {
        public String name;
        public String region;
        public String country;
    }

    public static class Current {
        public double temp_c;
        public double temp_f;
        public Condition condition;

        public static class Condition {
            public String text;
            public String icon; // URL icon
        }
    }
}

