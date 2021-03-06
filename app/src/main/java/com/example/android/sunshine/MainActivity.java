/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.Sync.SunshineSyncTask;
import com.example.android.sunshine.Sync.SunshineSyncUtils;
import com.example.android.sunshine.data.SunshineContract;
import com.example.android.sunshine.data.SunshineFakeDateByUdacity;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.NotificationUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements ForcastRecyclerViewAdapter.OnRecyclerViewItemClicked, LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int SEARCH_WEATHER_LOADER = 100;
    public static final String LOCATION_KEY = "PREFERED_LOCATION";
    SharedPreferences defaultSharedPreferences;
    TextView textViewDataError;
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    ForcastRecyclerViewAdapter adapter;
    Cursor dataSource;
    android.support.v4.app.LoaderManager loaderManager;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    public static final int OPEN_MAP_INTENT_ID=148;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0f);

        setContentView(R.layout.activity_forecast);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        adapter = new ForcastRecyclerViewAdapter(this, null, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        textViewDataError = (TextView) findViewById(R.id.tv_weather_data_error);
        progressBar = (ProgressBar) findViewById(R.id.progreesBar);
        loaderManager = getSupportLoaderManager();
        LoadWeatherData(MainActivity.this);
      //  getContentResolver().delete(SunshineContract.WeatherEntry.CONTENT_URI,null,null);
        SunshineSyncUtils.initialize(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Bundle bundle = new Bundle();
            String location = SunshinePreferences.getPreferredWeatherLocation(this);
            bundle.putString(LOCATION_KEY, location);
            loaderManager.restartLoader(SEARCH_WEATHER_LOADER, bundle, this);
        }
        PREFERENCES_HAVE_BEEN_UPDATED = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forcast, menu);
        return true;
    }
    private void OpenMaplLocation()
    {
        String currentLocation = defaultSharedPreferences.getString(getString(R.string.LocationPreferenceKey),
                getString(R.string.LocationPreferenceDefault));
        Uri.Builder builder=new Uri.Builder();
        Uri uri = builder.scheme("geo").encodedAuthority("0,0").appendQueryParameter("q", currentLocation).build();
        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent,OPEN_MAP_INTENT_ID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== OPEN_MAP_INTENT_ID)
        {
             if(resultCode == RESULT_OK){
                String latitude=data.getStringExtra("latitude");
                String longitude=data.getStringExtra("longitude");
            } else if (resultCode == RESULT_CANCELED) {
                //ActivityB was closed before you put any results
            }
//            SharedPreferences.Editor editor = defaultSharedPreferences.edit();
//            editor.putString(getString(R.string.LocationPreferenceKey),)
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapLocationItem:
                OpenMaplLocation();
                return true;
            case R.id.SettingItem:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }

    public void DisplayError() {
        textViewDataError.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    public void DisplayData() {
        recyclerView.setVisibility(View.VISIBLE);
        textViewDataError.setVisibility(View.INVISIBLE);
        //adapter.SetDataSource(dataSource);
        recyclerView.setAdapter(adapter);
    }

    private void LoadWeatherData(Context context) {
        // new FetcherWeatherTask().execute(SunshinePreferences.getPreferredWeatherLocation(context));
        Bundle bundle = new Bundle();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        bundle.putString(LOCATION_KEY, location);
        if (loaderManager.getLoader(SEARCH_WEATHER_LOADER) != null)
            loaderManager.initLoader(SEARCH_WEATHER_LOADER, bundle, this);
        else
            loaderManager.restartLoader(SEARCH_WEATHER_LOADER, bundle, this);
    }

    @Override
    public void OnRecyclerViewItemClicked(long date) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.WEATHER_ID_INTENT, date);
        startActivity(intent);
    }

    @Override
    public android.support.v4.content.Loader onCreateLoader(int id, final Bundle args) {
        return new CursorLoader(this, SunshineContract.WeatherEntry.CONTENT_URI, null, null, null, SunshineContract.WeatherEntry.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader loader, Cursor data) {
        progressBar.setVisibility(View.INVISIBLE);
        if (data == null) {
            DisplayError();
            return;
        }
        dataSource = data;
        adapter.SwapCursor(data);
        if (data.getCount() > 0)
            DisplayData();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {
        adapter.SwapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
        if(key.equals(getString(R.string.LocationPreferenceKey))) {
            getContentResolver().delete(SunshineContract.WeatherEntry.CONTENT_URI, null, null);
            SunshineSyncUtils.sInitialized=false;
            SunshineSyncUtils.initialize(this);
        }
    }

    public void ShowNotification(View view) {
        NotificationUtils.NotifyUserTodayaData(this);
    }


    class FetcherWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            if (strings.length <= 0) {
                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                return null;
            }
            URL url = null;
            try {
                url = NetworkUtils.buildUrl(strings[0], MainActivity.this);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                String responseFromHttpUrl = NetworkUtils.getResponseFromHttpUrl(url);
                String[] simpleWeatherStringsFromJson = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, responseFromHttpUrl);
                return simpleWeatherStringsFromJson;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.INVISIBLE);
            if (s == null) {
                DisplayError();
                return;
            }
            DisplayData();

        }
    }
}