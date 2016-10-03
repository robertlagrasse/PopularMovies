package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by robert on 9/10/16.
 * This fragment will present a gridview to the user, and use an interface to communicate
 * the user's choice back to the calling activity.
 */
public class GridFragment extends Fragment {

    // Provides the reference back to MainActivity
    Communicator communicator;

    // Place for the movies to wait for their turn in the gridview
    ArrayList<MovieObject> theHopper;

    // Takes in objects, spits out gridview food.
    ImageAdapter imageAdapter;

    private DatabaseAdapter database;

    @Override
    public void onResume() {
        super.onResume();

//        openDB();
//        Log.e("onResume", "Database Count: " + database.getDBCount());
//        closeDB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.grid_fragment, container, false);

        // This is a reference to the calling activity, and is what allows us
        // to send information back. Calling activity implements Communicator
        communicator = (Communicator) getActivity();

        // Build a place for a bunch of movie objects to wait for something to do
        theHopper = new ArrayList<>();

        // Build a reference to the gridview
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);

        // Build a new ImageAdapter to connect theHopper to the gridview
        imageAdapter = new ImageAdapter(getActivity(), R.id.gridview, theHopper);

        // Connect the imageAdapter to the gridview
        gridview.setAdapter(imageAdapter);

        // Make the gridview listen for clicks
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {

            // When someone clicks, do this:
                // Build an ArrayList to hold the information passed
                ArrayList<String> showTime = new ArrayList<>();

                // Populate the ArrayList with all of the elements in
                // the movie object referenced in the position.
                showTime.add(theHopper.get(position).getMovie_poster_path());
                showTime.add(String.valueOf(theHopper.get(position).getMovie_adult()));
                showTime.add(theHopper.get(position).getMovie_overview());
                showTime.add(theHopper.get(position).getMovie_release_date());
                showTime.add(theHopper.get(position).getMovie_genre_ids());
                showTime.add(String.valueOf(theHopper.get(position).getMovie_id()));
                showTime.add(theHopper.get(position).getMovie_original_title());
                showTime.add(theHopper.get(position).getMovie_original_language());
                showTime.add(theHopper.get(position).getMovie_title());
                showTime.add(theHopper.get(position).getMovie_backdrop_path());
                showTime.add(String.valueOf(theHopper.get(position).getMovie_popularity()));
                showTime.add(String.valueOf(theHopper.get(position).getMovie_vote_count()));
                showTime.add(String.valueOf(theHopper.get(position).getMovie_video()));
                showTime.add(String.valueOf(theHopper.get(position).getMovie_vote_average()));

                // Send the array list back to the Activity that spawned this fragment
                // The activity must implement Communicator, and must also implement
                // the .respond() method.
                communicator.respond(showTime);
            }
        });

        return rootView;
    }

    private void openDB() {
        database = new DatabaseAdapter(getActivity());
        database.open();
    }

    private void closeDB() {
        database.close();
    }

    // adapted from https://developer.android.com/guide/topics/ui/layout/gridview.html
    public class ImageAdapter extends ArrayAdapter<MovieObject> {
        private Context mContext;
        private ArrayList<MovieObject> mMovies;
        private int mResource;


        public ImageAdapter(Context context, int resource, ArrayList<MovieObject> objects) {
            super(context, resource, objects);
            mContext = context;
            mMovies = objects;
            mResource = resource;
        }

        public int getCount() {
            return mMovies.size();
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            MovieObject movie = getItem(position);

            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                // imageView.setLayoutParams(new GridView.LayoutParams(240, 240));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setAdjustViewBounds(true);
            } else {
                imageView = (ImageView) convertView;
            }
            String baseurl = "http://image.tmdb.org/t/p/w185";
            Picasso.with(getContext()).load(baseurl.concat(movie.getMovie_poster_path())).into(imageView);
            return imageView;
        }
    }
}
