package com.example.android.sunshine.Sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class SunshineFirebaseJobService extends JobService {
    AsyncTask task;
    @Override
    public boolean onStartJob(final JobParameters job) {
        final Context context=this;
        task=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                SunshineSyncTask.syncWeather(context);
                jobFinished(job,false);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

            }
        }.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(task!=null)
            task.cancel(true);
        return true;
    }
}
