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
import android.widget.EditText;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements Communicator {
    static final String GRID_FRAGMENT_TAG = "grid";
    static final String DISPLAY_FRAGMENT_TAG = "display";
    static final String LAST_SEEN_TAG = "LAST_SEEN_TAG";
    String LastVisible;
    boolean TwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an internet download task, and go get the data
        InternetDownloadTask pull = new InternetDownloadTask(this);
        pull.updateMovies();

        // When the application fires up, set the default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // If we're in two pane mode, I can see display_activity, so I'll have to fill that.
        if (findViewById(R.id.display_activity)!=null){
            TwoPane = true;
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            DisplayFragment displayFragment = new DisplayFragment();
            fragmentTransaction.add(R.id.display_activity, displayFragment, DISPLAY_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        else {
            TwoPane=false;
        }
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
        // Two pane mode, kick off a new Display Fragment
        if (TwoPane){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            DisplayFragment displayFragment = new DisplayFragment();
            fragmentTransaction.add(R.id.display_activity, displayFragment, DISPLAY_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        else {
            // One Pane mode - fire off a new Activity to handle the fragment
            Intent intent = new Intent(this, DisplayActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void likeButton() {
        // Someone liked a movie. Refresh the grid so those results are reflected
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        GridFragment gridFragment = new GridFragment();
        fragmentTransaction.add(R.id.activity_main, gridFragment, GRID_FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

    @Override
    protected void onStop() {
        super.onStop();
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
        LastVisible = savedInstanceState.getString(LAST_SEEN_TAG);
    }
}



