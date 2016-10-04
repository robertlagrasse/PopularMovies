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
import android.support.v4.widget.CursorAdapter;
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

import static android.content.Context.MODE_PRIVATE;

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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.grid_fragment, container, false);

        mContext = getActivity();
        communicator = (Communicator) getActivity();
        Cursor cursor;

        cursor = mContext.getContentResolver().query(
                TMDBContract.MovieEntry.buildTopRatedURI(),
                null,
                null,
                null,
                null);

        final gvCursorAdapter adapter = new gvCursorAdapter(
                getActivity(),
                cursor);

        // Build a reference to the gridview
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);

        // Connect the imageAdapter to the gridview
        gridview.setAdapter(adapter);

        // Make the gridview listen for clicks
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
            Log.e("GridView", "Position: " + position +" id: "+ id);
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
