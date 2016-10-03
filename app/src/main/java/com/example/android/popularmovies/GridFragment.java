package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
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

    Context mContext;
/*public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder)*/
    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();
        Cursor cursor;

        /*
         * This pulls an individual record
         *
        cursor = mContext.getContentResolver().query(
                TMDBContract.MovieEntry.buildMovieURI(389),
                TMDBContract.MovieEntry.MOVIE_ALL_KEYS,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            Log.e("GridFragment", "COLUMN_UID: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.COLUMN_UID)));
            Log.e("GridFragment", "MOVIE_POSTER_PATH: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_POSTER_PATH)));
            Log.e("GridFragment", "MOVIE_OVERVIEW: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_OVERVIEW)));
            Log.e("GridFragment", "MOVIE_RELEASE_DATE: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_RELEASE_DATE)));
            Log.e("GridFragment", "MOVIE_GENRE_IDS: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_GENRE_IDS)));
            Log.e("GridFragment", "MOVIE_ORIGINAL_TITLE: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_ORIGINAL_TITLE)));
            Log.e("GridFragment", "MOVIE_ORIGINAL_LANGUAGE: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_ORIGINAL_LANGUAGE)));
            Log.e("GridFragment", "MOVIE_TITLE: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_TITLE)));
            Log.e("GridFragment", "MOVIE_BACKDROP_PATH: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_BACKDROP_PATH)));
            Log.e("GridFragment", "MOVIE_POPULARITY: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_POPULARITY)));
            Log.e("GridFragment", "MOVIE_VOTE_COUNT: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_VOTE_COUNT)));
            Log.e("GridFragment", "MOVIE_VIDEO: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_VIDEO)));
            Log.e("GridFragment", "MOVIE_VOTE_AVERAGE: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_VOTE_AVERAGE)));
            Log.e("GridFragment", "MOVIE_TOP_RATED: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_TOP_RATED)));
            Log.e("GridFragment", "MOVIE_MOST_POPULAR: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_MOST_POPULAR)));
            Log.e("GridFragment", "MOVIE_USER_FAVORITE: " + cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_USER_FAVORITE)));

        } else {
            Log.e("GridGragment", "Cursor returned no rows");
        }
        cursor.close();
        */

        cursor = mContext.getContentResolver().query(
                TMDBContract.MovieEntry.buildTopRatedURI(),
                new String[]{TMDBContract.MovieEntry.MOVIE_TITLE, TMDBContract.MovieEntry.MOVIE_ID},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, cv);
                Log.e("GridFragment", cv.getAsString(TMDBContract.MovieEntry.MOVIE_TITLE));
                Log.e("GridFragment", cv.getAsString(TMDBContract.MovieEntry.MOVIE_ID));
            } while (cursor.moveToNext());

        } else {
            Log.e("GridGragment", "Cursor returned no rows");
        }
        cursor.close();

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
