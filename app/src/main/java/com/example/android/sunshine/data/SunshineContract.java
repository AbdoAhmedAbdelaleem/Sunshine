package com.example.android.sunshine.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class SunshineContract
{
    public static final String AUTHORITY="com.example.android.sunshine";
    public static final String WEATHER_PATH="WEATHER";
    public static final Uri BASE_WEATHER_URI= Uri.parse("conten://"+AUTHORITY);

    public static class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI=BASE_WEATHER_URI.buildUpon().appendPath(WEATHER_PATH).build();
        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_WEATHER_ID = "weather_id";

        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        public static final String COLUMN_HUMIDITY = "humidity";

        public static final String COLUMN_PRESSURE = "pressure";

        public static final String COLUMN_WIND_SPEED = "wind";

        public static final String COLUMN_DEGREES = "degrees";
    }
}
