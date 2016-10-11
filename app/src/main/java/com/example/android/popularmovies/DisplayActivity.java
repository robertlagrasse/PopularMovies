package com.example.android.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


/**
 * Created by robert on 10/10/16.
 */

public class DisplayActivity extends AppCompatActivity {
    static final String GRID_FRAGMENT_TAG = "grid";
    static final String DISPLAY_FRAGMENT_TAG = "display";
    static final String LAST_SEEN_TAG = "LAST_SEEN_TAG";
    String LOG = "DisplayActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activity);

        Log.e(LOG, "onCreate");

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        DisplayFragment displayFragment = new DisplayFragment();
        fragmentTransaction.add(R.id.display_activity, displayFragment, DISPLAY_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }
}