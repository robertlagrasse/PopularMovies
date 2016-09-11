package com.example.android.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements Communicator{
    public final static String EXTRA_MESSAGE = "com.example.android.popularmovies.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a FragmentManager and start a fragmentTransaction
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create an instance of GridFragment and drop it on activity_main
        GridFragment gridFragment = new GridFragment();
        fragmentTransaction.add(R.id.activity_main, gridFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void respond(int data) {
        Log.e("MainActivity", "Data passed via Interface: " + data);

        String message = String.valueOf(data);
        Intent intent = new Intent (this, DisplayActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}



