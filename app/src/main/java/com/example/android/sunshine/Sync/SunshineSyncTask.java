package com.example.android.sunshine.Sync;

import android.content.ContentValues;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.example.android.sunshine.MainActivity;
import com.example.android.sunshine.R;
import com.example.android.sunshine.data.SunshineContract;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.NotificationUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SunshineSyncTask
{
    public static synchronized void syncWeather(Context context)
    {
        URL url = null;
        try {
            url = NetworkUtils.getUrl(context);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            String responseFromHttpUrl = NetworkUtils.getResponseFromHttpUrl(url);
            ContentValues[] data = OpenWeatherJsonUtils.getFullWeatherDataFromJson(context, responseFromHttpUrl);
             if(data!=null && data.length>0)
             {
                 context.getContentResolver().delete(SunshineContract.WeatherEntry.CONTENT_URI,null,null);
                context.getContentResolver().bulkInsert(SunshineContract.WeatherEntry.CONTENT_URI,data);
             }

            long timeSinceLastNotification = SunshinePreferences
                    .getEllapsedTimeSinceLastNotification(context);

            boolean oneDayPassedSinceLastNotification = false;
            if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                oneDayPassedSinceLastNotification = true;
            }
          boolean notificationsEnabled= PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                  context.getString(R.string.pref_enable_notifications_key),true);
           // if (notificationsEnabled && oneDayPassedSinceLastNotification)
            {
                NotificationUtils.NotifyUserTodayaData(context);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
