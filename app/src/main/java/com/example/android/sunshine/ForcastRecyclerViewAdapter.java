package com.example.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.data.SunshineContract.*;

import com.example.android.sunshine.data.SunshineContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

import java.util.ArrayList;

public class ForcastRecyclerViewAdapter extends RecyclerView.Adapter<ForcastRecyclerViewAdapter.ForcastViewHolder> {
    OnRecyclerViewItemClicked onRecyclerViewItemClicked;
    Context context;
    Cursor dataSource;

    public ForcastRecyclerViewAdapter(Context context, Cursor dataSource, OnRecyclerViewItemClicked onRecyclerViewItemClicked) {
        this.context = context;
        this.dataSource = dataSource;
        this.onRecyclerViewItemClicked = onRecyclerViewItemClicked;
    }

    @Override
    public ForcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.recycler_view_item, parent, false);
        return new ForcastViewHolder(view);
    }

    public void SwapCursor(Cursor cursor) {
        if (cursor != null) {
            if (dataSource != null)
                dataSource.close();
            dataSource = cursor;
            notifyDataSetChanged();
        }
    }

    public void SetDataSource(Cursor dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ForcastViewHolder holder, int position) {
        holder.OnBind(position);
    }

    @Override
    public int getItemCount() {
        if (dataSource == null)
            return 0;
        return dataSource.getCount();
    }

    public class ForcastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewDate;
        TextView textViewDescription;
        TextView textViewHighTemperature;
        TextView textViewLowTemperature;
        ImageView iconWeather;


        public ForcastViewHolder(View itemView) {
            super(itemView);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            textViewDescription = (TextView) itemView.findViewById(R.id.textViewDecription);
            textViewHighTemperature = (TextView) itemView.findViewById(R.id.textViewHighTemperature);
            textViewLowTemperature = (TextView) itemView.findViewById(R.id.textViewLowTemperature);
            iconWeather = (ImageView) itemView.findViewById(R.id.imageViewWeatherImage);
        }

        public void OnBind(int position) {
            if (dataSource.moveToPosition(position)) {
                int columnDateIndex = dataSource.getColumnIndex(WeatherEntry.COLUMN_DATE);
                int columnWeatherIDIndex = dataSource.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
                int columnWeatherMaxTempIndex = dataSource.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP);
                int columnWeatherMinTempIndex = dataSource.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP);
                long dateMillis = dataSource.getLong(columnDateIndex);
                String dateString = SunshineDateUtils.getFriendlyDateString(context, dateMillis, true);
                int weatherID = dataSource.getInt(columnWeatherIDIndex);
                String description = SunshineWeatherUtils.getStringForWeatherCondition(context, weatherID);
                double maxTemp = dataSource.getDouble(columnWeatherMaxTempIndex);
                double minTemp = dataSource.getDouble(columnWeatherMinTempIndex);
                //  String highAndLowTemperature = SunshineWeatherUtils.formatHighLows(context, maxTemp, minTemp);
                String highTemperature = SunshineWeatherUtils.formatTemperature(context, maxTemp);
                String minTemperature = SunshineWeatherUtils.formatTemperature(context, minTemp);
                //   String weatherSummary=dateString+" - "+description+" - "+highAndLowTemperature;
                textViewDate.setText(dateString);
                textViewDescription.setText(description);
                textViewHighTemperature.setText(highTemperature);
                textViewHighTemperature.setText(minTemperature);
                int weatherImageId = SunshineWeatherUtils
                        .getArtResourceForWeatherCondition(weatherID);
                iconWeather.setImageResource(weatherImageId);
                this.itemView.setTag(dateMillis);
            }
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            onRecyclerViewItemClicked.OnRecyclerViewItemClicked((long) itemView.getTag());
        }
    }

    interface OnRecyclerViewItemClicked {
        public void OnRecyclerViewItemClicked(long date);
    }
}
