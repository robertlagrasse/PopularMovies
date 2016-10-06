package com.example.android.popularmovies;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_POSTER_PATH;


public class MainActivity extends AppCompatActivity implements Communicator {
    String GRID_FRAGMENT_TAG = "grid";
    String DISPLAY_FRAGMENT_TAG = "display";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InternetDownloadTask pull = new InternetDownloadTask(this);
        pull.updateMovies();

        // When the application fires up, set the default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Create a FragmentManager and start a fragmentTransaction
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create an instance of GridFragment and drop it on activity_main
        GridFragment gridFragment = new GridFragment();
        fragmentTransaction.add(R.id.activity_main, gridFragment, GRID_FRAGMENT_TAG);
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
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public void respond(long data) {
        ContentValues values = new ContentValues();
        values.put(TMDBContract.UserMetrics.COLUMN_SELECTED_MOVIE, data);

        Uri insertedUri = this.getContentResolver().insert(
                TMDBContract.buildUserSelectionURI(),
                values
        );

        Log.e("MainActivity", "Returned URI: " + insertedUri.toString());

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create an instance of GridFragment and drop it on activity_main
        DisplayFragment displayFragment = new DisplayFragment();
        // displayFragment.setPassedMovie(data);

        fragmentTransaction.addToBackStack(GRID_FRAGMENT_TAG);
        fragmentTransaction.replace(R.id.activity_main, displayFragment, DISPLAY_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }
}



