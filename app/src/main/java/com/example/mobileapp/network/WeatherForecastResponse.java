package com.example.mobileapp.network;

import java.util.List;

public class WeatherForecastResponse {
    public Forecast forecast;

    public static class Forecast {
        public List<ForecastDay> forecastday;
    }

    public static class ForecastDay {
        public Day day;
        public String date;
    }

    public static class Day {
        public float avgtemp_c;
        public Condition condition;
    }

    public static class Condition {
        public String text;
        public String icon;
    }
}
