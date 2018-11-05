package com.example.android.sunshine.Sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.android.sunshine.data.SunshineContract;

public class SunshineSyncUtils
{
    public static boolean sInitialized;
    public static void initialize(final Context context)
    {
        if(sInitialized)
            return;
        sInitialized=true;
        new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected Cursor doInBackground(Void... voids) {
                Cursor query = context.getContentResolver().query(SunshineContract.WeatherEntry.CONTENT_URI, null, null, null, null);
                return query;
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                super.onPostExecute(cursor);
                if(cursor==null || cursor.getCount()==0)
                {
                    SunshineSyncUtils.StartImmediateSync(context);
                }
            }
        }.execute();
    }
    public static void StartImmediateSync(Context context) {
        Intent intent = new Intent(context, SunshineIntentService.class);
        context.startService(intent);
    }
}
