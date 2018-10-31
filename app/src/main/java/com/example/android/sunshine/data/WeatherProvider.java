package com.example.android.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class WeatherProvider extends ContentProvider {
   SunshineDbHelper dbHelper;
   public static UriMatcher sUriMatcher=BuildUriMatcher();
    public static final int WEATHER=100;
    public static final int WEATHER_WITH_ID=101;
   public static UriMatcher BuildUriMatcher()
   {
       UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
       matcher.addURI(SunshineContract.AUTHORITY,SunshineContract.WEATHER_PATH,WEATHER);
       matcher.addURI(SunshineContract.AUTHORITY,SunshineContract.WEATHER_PATH+"/#",WEATHER_WITH_ID);
       return matcher;
   }
   @Override
    public boolean onCreate() {
        dbHelper=new SunshineDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
