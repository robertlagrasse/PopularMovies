package com.example.android.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


/**
 * Created by robert on 10/10/16.
 * Display activity is responsible for the launch of the DisplayFragment in single pane mode.
 * This does not get invoked in two pane mode.
 *
 * repond() and likeButton() are required as the activity implements Communicator, and the
 * DisplayFragment will call it.
 */

public class DisplayActivity extends AppCompatActivity implements Communicator{
    static final String DISPLAY_FRAGMENT_TAG = "display";
    static final String LAST_SEEN_TAG = "LAST_SEEN_TAG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activity);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        DisplayFragment displayFragment = new DisplayFragment();
        fragmentTransaction.add(R.id.display_activity, displayFragment, DISPLAY_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void respond() {
        // nothing to do!
    }

    @Override
    public void likeButton() {
        // nothing to do!
    }
}
