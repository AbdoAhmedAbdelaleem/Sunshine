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
import android.content.Loader;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;
import com.example.android.sunshine.utilities.SunshineDateUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ForcastRecyclerViewAdapter.OnRecyclerViewItemClicked, LoaderManager.LoaderCallbacks<String[]> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int SEARCH_WEATHER_LOADER = 100;
    public static final String LOCATION_KEY = "PREFERED_LOCATION";
    public static String sunshinePreferenceLocation;
    TextView textViewDataError;
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    ForcastRecyclerViewAdapter adapter;
    ArrayList<String> dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        dataSource = new ArrayList<>();
        sunshinePreferenceLocation = SunshinePreferences.getPreferredWeatherLocation(this);
        adapter = new ForcastRecyclerViewAdapter(this, dataSource, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        textViewDataError = (TextView) findViewById(R.id.tv_weather_data_error);
        progressBar = (ProgressBar) findViewById(R.id.progreesBar);
        LoadWeatherData(MainActivity.this);
        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putString(LOCATION_KEY, sunshinePreferenceLocation);
        if (loaderManager.getLoader(SEARCH_WEATHER_LOADER) != null)
            loaderManager.initLoader(SEARCH_WEATHER_LOADER, bundle, this);
        else
            loaderManager.restartLoader(SEARCH_WEATHER_LOADER, bundle, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forcast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.RefreshButton) {
            LoadWeatherData(MainActivity.this);
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    public void DisplayError() {
        textViewDataError.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    public void DisplayData() {
        recyclerView.setVisibility(View.VISIBLE);
        textViewDataError.setVisibility(View.INVISIBLE);
        adapter.SetDataSource(dataSource);
        recyclerView.setAdapter(adapter);
    }

    private void LoadWeatherData(Context context) {
        // new FetcherWeatherTask().execute(SunshinePreferences.getPreferredWeatherLocation(context));
    }

    @Override
    public void OnRecyclerViewItemClicked(int position) {
        String currentData = dataSource.get(position);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_TEXT, currentData);
        startActivity(intent);
    }

    @Override
    public android.support.v4.content.Loader onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader(this) {
            String[] weatherData;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args.size() <= 0) {
                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if(weatherData!=null)
                {
                    deliverResult(weatherData);
                }
                else {
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(Object data)
            {
                weatherData= (String[]) data;
                super.deliverResult(data);

            }

            @Override
            public String[] loadInBackground() {
                URL url = null;
                try {
                    url = NetworkUtils.buildUrl(args.getString(LOCATION_KEY));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    if(url!=null) {
                        String responseFromHttpUrl = NetworkUtils.getResponseFromHttpUrl(url);
                        String[] simpleWeatherStringsFromJson = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, responseFromHttpUrl);
                        weatherData = simpleWeatherStringsFromJson;
                        return simpleWeatherStringsFromJson;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader loader, String[] data) {
        progressBar.setVisibility(View.INVISIBLE);
        if (data == null) {
            DisplayError();
            return;
        }
        dataSource = new ArrayList<>();
        for (String str : data) {
            dataSource.add(str);
        }
        DisplayData();

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {

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
                url = NetworkUtils.buildUrl(strings[0]);
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
            dataSource = new ArrayList<>();
            for (String str : s) {
                dataSource.add(str);
            }
            DisplayData();

        }
    }
}