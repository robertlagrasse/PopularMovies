package com.example.android.popularmovies;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Communicator{
    public final static String EXTRA_MESSAGE = "com.example.android.popularmovies.MESSAGE";
    public final static String MOVIE_DETAILS = "movie_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // When the application fires up, set the default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Create a FragmentManager and start a fragmentTransaction
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create an instance of GridFragment and drop it on activity_main
        GridFragment gridFragment = new GridFragment();
        // TMDBQueryManagerFragment tmdbQueryManagerFragment = new TMDBQueryManagerFragment();
        fragmentTransaction.add(R.id.activity_main, gridFragment);
        // fragmentTransaction.add(tmdbQueryManagerFragment, "tmdb-qmf");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_settings:
                Intent intent = new Intent (this, MenuActivity.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public void respond(ArrayList<String> data) {

        // An ArrayList called data has arrived. It needs to be passed via Intent
        // to DisplayActivity.

        // First, create the intent
        Intent showMovieDetails = new Intent (this, DisplayActivity.class);

        // Next, attach the data to the intent
        showMovieDetails.putExtra(MOVIE_DETAILS, data);

        // Finally, start the activity
        startActivity(showMovieDetails);
    }
}



