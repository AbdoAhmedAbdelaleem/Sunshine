package com.example.android.sunshine;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.SunshineContract.*;

import com.example.android.sunshine.databinding.ActivityDetailBinding;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String WEATHER_ID_INTENT = "WEATHER_ID_INTENT";
    public static final String WEATHER_ID_BUNDLE = "WEATHER_ID_Bundle";
    public static final int LOADER_ID = 789;
    ActivityDetailBinding mDetailBinding;
    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
    };
    public static final int COLUMN_DATE_INDEX = 0;
    public static final int COLUMN_WEATHER_ID_INDEX = 1;
    public static final int COLUMN_MAX_TEMP_INDEX = 2;
    public static final int COLUMN_MIN_TEMP_INDEX = 3;
    public static final int COLUMN_HUMIDITY_INDEX = 4;
    public static final int COLUMN_PRESSURE_INDEX = 5;
    public static final int COLUMN_WIND_SPEED_INDEX = 6;
    public static final int COLUMN_DEGREES_INDEX = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        Intent intent = getIntent();
        if (intent.hasExtra(WEATHER_ID_INTENT)) {
            long date = intent.getLongExtra(WEATHER_ID_INTENT, -1);
            if (date != -1) {
                Bundle bundle = new Bundle();
                bundle.putLong(WEATHER_ID_BUNDLE, date);
                getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent.hasExtra(WEATHER_ID_INTENT)) {
            long date = intent.getLongExtra(WEATHER_ID_INTENT, -1);
            if (date != -1) {
                Bundle bundle = new Bundle();
                bundle.putLong(WEATHER_ID_BUNDLE, date);
                getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareItem:
                ShareData();
                return true;
            case R.id.SettingItem:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }

    private void ShareData() {
        String textViewWeatherDate= mDetailBinding.PrimaryWeatherInfo.tvWeatherDate.getText().toString();
        String textViewWeatherDescription= mDetailBinding.PrimaryWeatherInfo.tvWeatherDescription.getText().toString();
        String maxTemp= mDetailBinding.PrimaryWeatherInfo.tvWeatherMaxTemperature.getText().toString();
        String minTemp= mDetailBinding.PrimaryWeatherInfo.tvWeatherMinTemperature.getText().toString();
        String weatherSummary = textViewWeatherDate + " - " +textViewWeatherDescription +
                " - " +maxTemp+" / "+minTemp;
        ShareCompat.IntentBuilder.from(this)
                .setChooserTitle("Weather")
                .setText(weatherSummary)
                .setType("text/plain")
                .startChooser();
    }

    private double toDouble(String str) {
        return Double.parseDouble(str);
    }

    private String getTextViewValue(TextView editText) {
        return editText.getText().toString();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long date = args.getLong(WEATHER_ID_BUNDLE);
        Uri selectedItemUri = ContentUris.withAppendedId(WeatherEntry.CONTENT_URI, date);
        return new CursorLoader(this, selectedItemUri, WEATHER_DETAIL_PROJECTION, null, null, WeatherEntry.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            long dateMillis = data.getLong(COLUMN_DATE_INDEX);
            String dateString = SunshineDateUtils.getFriendlyDateString(this, dateMillis, true);
            mDetailBinding.PrimaryWeatherInfo.tvWeatherDate.setText(dateString);

            int weatherID = data.getInt(COLUMN_WEATHER_ID_INDEX);
            String description = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherID);
            mDetailBinding.PrimaryWeatherInfo.tvWeatherDescription.setText(description);


            float maxTemp =  data.getFloat(COLUMN_MAX_TEMP_INDEX);
            float minTemp = data.getFloat(COLUMN_MIN_TEMP_INDEX);
            String max=SunshineWeatherUtils.formatTemperature(this,maxTemp)+"";
            String min=SunshineWeatherUtils.formatTemperature(this,minTemp)+"";
            mDetailBinding.PrimaryWeatherInfo.tvWeatherMaxTemperature.setText(max);
            mDetailBinding.PrimaryWeatherInfo.tvWeatherMinTemperature.setText(min);

            float humidity = data.getFloat(COLUMN_HUMIDITY_INDEX);
            String humidityString = ((int) humidity) + " %";
            mDetailBinding.extraDetails.humidity.setText(humidityString);

            float pressure = data.getFloat(COLUMN_PRESSURE_INDEX);
            String pressureString = ((int) pressure) + " hPa";
            mDetailBinding.extraDetails.pressure.setText(pressureString);

            float windSpeed = data.getFloat(COLUMN_WIND_SPEED_INDEX);
            float windDegree = data.getFloat(COLUMN_DEGREES_INDEX);
            String windString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDegree);
            mDetailBinding.extraDetails.windMeasurement.setText(windString);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
