package com.example.android.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.sunshine.utilities.SunshineDateUtils;

public class WeatherProvider extends ContentProvider {
    SunshineDbHelper dbHelper;
    public static UriMatcher sUriMatcher = BuildUriMatcher();
    public static final int WEATHER = 100;
    public static final int WEATHER_WITH_ID = 101;

    public static UriMatcher BuildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(SunshineContract.AUTHORITY, SunshineContract.WEATHER_PATH, WEATHER);
        matcher.addURI(SunshineContract.AUTHORITY, SunshineContract.WEATHER_PATH + "/#", WEATHER_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new SunshineDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int match = sUriMatcher.match(uri);
        int numOfRowsInserted = 0;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        switch (match) {
            case WEATHER:
                database.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        Long date = value.getAsLong(SunshineContract.WeatherEntry.COLUMN_DATE);
                        if (SunshineDateUtils.isDateNormalized(date)) {
                            throw new IllegalArgumentException("Date must be normalized");
                        }
                        long l = database.insert(SunshineContract.WeatherEntry.TABLE_NAME, null, value);
                        if (l != -1)
                            numOfRowsInserted++;
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                if(numOfRowsInserted>0)
                    getContext().getContentResolver().notifyChange(uri,null);
                return numOfRowsInserted;
        }
        return super.bulkInsert(uri, values);
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
