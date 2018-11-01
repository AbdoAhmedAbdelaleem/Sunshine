package com.example.android.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.android.sunshine.data.SunshineContract.*;
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
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortedBy) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor=null;
        switch (match)
        {
            case WEATHER:
              cursor=  database.query(SunshineContract.WeatherEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortedBy);

                break;
            case WEATHER_WITH_ID:
                String dateParameter=uri.getLastPathSegment();
                selectionArgs=new String[]{dateParameter};
               cursor= database.query(SunshineContract.WeatherEntry.TABLE_NAME,projection,WeatherEntry.COLUMN_DATE+"=?",selectionArgs,null,null,sortedBy);

                break;
                default:
                    throw new UnsupportedOperationException("this Uri not supported");
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
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
                        if (!SunshineDateUtils.isDateNormalized(date)) {
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
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int numOfRowsDeleted=0;
        switch (match)
        {
            case WEATHER:
               numOfRowsDeleted= database.delete(SunshineContract.WeatherEntry.TABLE_NAME,s,strings);
                break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
