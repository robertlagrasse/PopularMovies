package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activity);

        Intent intent = getIntent();
        String value = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Log.e("DisplayActivity.class", value);

        ImageView poster = (ImageView) findViewById(R.id.poster);

        Picasso.with(this)
                .load("http://www.gstatic.com/tv/thumb/movieposters/4363/p4363_p_v8_aa.jpg")
                .into(poster);
    }
}
