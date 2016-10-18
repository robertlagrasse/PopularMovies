package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


/**
 * Created by robert on 9/10/16.
 * This fragment will present a gridview to the user, and use an interface to communicate
 * the user's choice back to the calling activity.
 */
public class GridFragment extends Fragment {

    // Provides the reference back to MainActivity
    Communicator communicator;
    View theView;

    @Override
    public void onResume() {
        super.onResume();
        Context mContext = getActivity();
        communicator = (Communicator) getActivity();

        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = userPreferences.getString(getString(R.string.preferences_key_sort_by),getString(R.string.preferences_entryValue_sort_by_rating));

        Uri searchtype = TMDBContract.buildPopularURI();
        if (sortBy.equals("sort_by_rating")){
            searchtype = TMDBContract.buildTopRatedURI();
        }
        if (sortBy.equals("sort_by_favorites")){
            searchtype = TMDBContract.buildFavoritesURI();
        }

        Cursor cursor = mContext.getContentResolver().query(
                searchtype,
                null,
                null,
                null,
                null);

        gvCursorAdapter adapter = new gvCursorAdapter(
                getActivity(),
                cursor);

        GridView gridview = (GridView) theView.findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {

                ContentValues values = new ContentValues();
                values.put(TMDBContract.UserMetrics.COLUMN_SELECTED_MOVIE, id);

                Uri insertedUri = getActivity().getContentResolver().insert(
                        TMDBContract.buildUserSelectionURI(),
                        values
                );
                communicator.respond();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.grid_fragment, container, false);
        Log.e("Grid","onCreateView");
        theView = rootView;
        return rootView;
    }

}
