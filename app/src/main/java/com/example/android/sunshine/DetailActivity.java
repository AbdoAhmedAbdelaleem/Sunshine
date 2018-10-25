package com.example.android.sunshine;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_TEXT = "EXTRA_TEXT";
    TextView textViewDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        textViewDetails = (TextView) findViewById(R.id.tv_detailActivity);
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_TEXT)) {
            textViewDetails.setText(intent.getStringExtra(EXTRA_TEXT));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.shareItem) {
            ShareData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShareData() {
        ShareCompat.IntentBuilder.from(this)
                .setChooserTitle("Weather")
                .setText(textViewDetails.getText())
                .setType("text/plain")
                .startChooser();
    }
}
