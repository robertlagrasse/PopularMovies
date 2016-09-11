package com.example.android.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by robert on 9/10/16.
 * This fragment will present a gridview to the user, and use an interface to communicate
 * the user's choice back to the calling activity.
 */
public class GridFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.grid_fragment, container, false);
    }
}
