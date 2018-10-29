package com.example.android.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.sunshine.data.SunshineContract.WeatherEntry;

public class SunshineDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Weather.db";
    public static final int DATABASE_VERSION = 1;

    public SunshineDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String weatherEntryCreationString = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                WeatherEntry.COLUMN_DATE + " INTEGER, " +

                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER, " +

                WeatherEntry.COLUMN_MIN_TEMP + " REAL, " +

                WeatherEntry.COLUMN_MAX_TEMP + " REAL, " +

                WeatherEntry.COLUMN_HUMIDITY + " REAL, " +

                WeatherEntry.COLUMN_PRESSURE + " REAL, " +

                WeatherEntry.COLUMN_WIND_SPEED + " REAL, " +
                WeatherEntry.COLUMN_DEGREES + " REAL );";

        sqLiteDatabase.execSQL(weatherEntryCreationString);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
    }
}
