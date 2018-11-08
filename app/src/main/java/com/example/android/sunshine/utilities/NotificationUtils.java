package com.example.android.sunshine.utilities;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.android.sunshine.DetailActivity;
import com.example.android.sunshine.R;
import com.example.android.sunshine.data.SunshineContract;
import com.example.android.sunshine.data.SunshinePreferences;

import static com.example.android.sunshine.DetailActivity.COLUMN_DATE_INDEX;
import static com.example.android.sunshine.DetailActivity.COLUMN_WEATHER_ID_INDEX;

public class NotificationUtils
{

    public static final  int PENDING_INTENT_ID=4712;
    public static final  int NOTIFICATION_ID=21592;
    public static void NotifyUserTodayaData(Context context) {
        Cursor data = GetTodayDataFromProvider(context);

        if (data != null && data.moveToFirst()) {

            int weatherID = data.getInt(data.getColumnIndex(SunshineContract.WeatherEntry.COLUMN_WEATHER_ID));
            String description = SunshineWeatherUtils.getStringForWeatherCondition(context, weatherID);

            float maxTemp = data.getFloat(data.getColumnIndex(SunshineContract.WeatherEntry.COLUMN_MAX_TEMP));
            float minTemp = data.getFloat(data.getColumnIndex(SunshineContract.WeatherEntry.COLUMN_MIN_TEMP));
            String formatHighLows = SunshineWeatherUtils.formatHighLows(context, maxTemp, minTemp);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Notification notification = builder.setContentIntent(CreatePendingIntent(context))
                    .setContentTitle("Sunshine")
                    .setContentText("Today weather is"+description+" , "+formatHighLows)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_light_rain))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.art_clear).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
            SunshinePreferences.saveLastTime(context);
        }
    }
private static Cursor GetTodayDataFromProvider(Context context)
{
    long today = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
    Uri todayUri = SunshineContract.WeatherEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(today) ).build();
    Cursor query = context.getContentResolver().query(todayUri, null, null, null, null);
    return query;

}
    private static PendingIntent CreatePendingIntent(Context context)
    {
        Intent intent=new Intent(context, DetailActivity.class);
        long l = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
        intent.putExtra(DetailActivity.WEATHER_ID_INTENT,l);
        return PendingIntent.getActivity(context,PENDING_INTENT_ID,intent,PendingIntent.FLAG_UPDATE_CURRENT);

    }
}
