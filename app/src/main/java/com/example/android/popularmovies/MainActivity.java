package com.example.android.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

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
}



