package com.example.android.sunshine.Sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.android.sunshine.data.SunshineContract;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.security.PublicKey;

public class SunshineSyncUtils
{
    private static  final int NUM_OF_HOURS=3;
    public static  final int NUM_OF_SECONDS=3*60*60;
    public static final String SUNSHINE_SYNC_TAG = "sunshine-sync";
    public static boolean sInitialized;
    public static void SchaduleJobDispatcher(Context context)
    {
        FirebaseJobDispatcher dispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job build = dispatcher.newJobBuilder()
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(20, 40))
                .setLifetime(Lifetime.FOREVER)
                .setTag(SUNSHINE_SYNC_TAG)
                .setService(SunshineFirebaseJobService.class)
                .setReplaceCurrent(true)
                .build();
        dispatcher.mustSchedule(build);
    }
    public static void initialize(final Context context)
    {
        if(sInitialized)
            return;
        sInitialized=true;
        SchaduleJobDispatcher(context);
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
