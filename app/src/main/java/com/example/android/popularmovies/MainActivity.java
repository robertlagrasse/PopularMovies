package com.example.android.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements Communicator {
    static final String GRID_FRAGMENT_TAG = "grid";
    static final String DISPLAY_FRAGMENT_TAG = "display";
    static final String LAST_SEEN_TAG = "LAST_SEEN_TAG";
    String LastVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null){
            LastVisible = savedInstanceState.getString(LAST_SEEN_TAG);
        }
        else {
            LastVisible = GRID_FRAGMENT_TAG;
        }
        Log.e("onCreate()", "LastVisible: " + LastVisible);

        InternetDownloadTask pull = new InternetDownloadTask(this);
        pull.updateMovies();

        // When the application fires up, set the default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Create a FragmentManager and start a fragmentTransaction
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (LastVisible) {
            case DISPLAY_FRAGMENT_TAG: {
                Log.e("onCreate()","DISPLAY_FRAGMENT_TAG case reached.");
                DisplayFragment displayFragment = new DisplayFragment();
                fragmentTransaction.replace(R.id.activity_main, displayFragment, DISPLAY_FRAGMENT_TAG);
                break;
            }
            default: {
                Log.e("onCreate()","default case reached.");
                GridFragment gridFragment = new GridFragment();
                fragmentTransaction.add(R.id.activity_main, gridFragment, GRID_FRAGMENT_TAG);
            }
        }
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
    public void respond() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create an instance of GridFragment and drop it on activity_main
        DisplayFragment displayFragment = new DisplayFragment();
        // displayFragment.setPassedMovie(data);

        fragmentTransaction.addToBackStack(GRID_FRAGMENT_TAG);
        fragmentTransaction.replace(R.id.activity_main, displayFragment, DISPLAY_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("MainActivity", "onStop() called.");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        DisplayFragment displayTest = (DisplayFragment) getFragmentManager().findFragmentByTag(DISPLAY_FRAGMENT_TAG);
        if (displayTest != null && displayTest.isVisible()) {
            savedInstanceState.putString(LAST_SEEN_TAG , DISPLAY_FRAGMENT_TAG);
        }
        else {
            savedInstanceState.putString(LAST_SEEN_TAG , GRID_FRAGMENT_TAG);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("onRestoreInstanceState","onRestoreInstanceState");
        LastVisible = savedInstanceState.getString(LAST_SEEN_TAG);
        Log.e("onRestoreInstanceState","LastVisible was: " + LastVisible);
    }
}



