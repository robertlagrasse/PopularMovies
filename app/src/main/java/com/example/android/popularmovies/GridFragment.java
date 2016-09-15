package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.List;

/**
 * Created by robert on 9/10/16.
 * This fragment will present a gridview to the user, and use an interface to communicate
 * the user's choice back to the calling activity.
 */
public class GridFragment extends Fragment {

    Communicator communicator;
    ArrayList<MovieObject> theHopper;
    ImageAdapter imageAdapter;

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = userPreferences.getString(getString(R.string.preferences_key_sort_by),getString(R.string.preferences_entryValue_sort_by_rating));
        updateMovies(sortBy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.grid_fragment, container, false);

        communicator = (Communicator) getActivity();
        // Build a place for a bunch of movie objects to wait for something to do
        theHopper = new ArrayList<>();

        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = userPreferences.getString(getString(R.string.preferences_key_sort_by),getString(R.string.preferences_entryValue_sort_by_rating));
        // Go build the movie objects and put them in theHopper
        updateMovies(sortBy);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(getActivity(), R.id.gridview, theHopper);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {

                // Build an ArrayList to hold the information passed
                ArrayList<String> showTime = new ArrayList<String>();

                // Populate the ArrayList
                showTime.add(theHopper.get(position).getMovie_poster_path());
                showTime.add(theHopper.get(position).getMovie_adult());
                showTime.add(theHopper.get(position).getMovie_overview());
                showTime.add(theHopper.get(position).getMovie_release_date());
                showTime.add(theHopper.get(position).getMovie_genre_ids());
                showTime.add(theHopper.get(position).getMovie_id());
                showTime.add(theHopper.get(position).getMovie_original_title());
                showTime.add(theHopper.get(position).getMovie_original_language());
                showTime.add(theHopper.get(position).getMovie_title());
                showTime.add(theHopper.get(position).getMovie_backdrop_path());
                showTime.add(theHopper.get(position).getMovie_popularity());
                showTime.add(theHopper.get(position).getMovie_vote_count());
                showTime.add(theHopper.get(position).getMovie_video());
                showTime.add(theHopper.get(position).getMovie_vote_average());

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
        }

        public int getCount() {
            return mMovies.size();
        }

        public long getItemId(int position) {
            return 0;
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

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieObject>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected ArrayList<MovieObject> doInBackground(String... sortOrder) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String rawData = null;

            try {
                // Constants to identify parameters and corresponding values
                // for use in the construction of the URL

                final String PARAMETER_BASE_URL                 =   null;
                final String VALUE_BASE_URL                     =   "https://api.themoviedb.org/3/discover/movie?";

                final String PARAMETER_SORT_BY                  =   "sort_by";
                final String VALUE_SORT_BY_POPULARITY           =   "popularity.desc";
                final String VALUE_SORT_BY_RATING               =   "vote_average.desc";

                final String PARAMETER_API_KEY                  =   "api_key";
                final String VALUE_API_KEY                      =   "f42ec8a4b30bcaf191a165668a819fda";


            String value_sort_by = null;
             if (sortOrder[0].equals("sort_by_popularity")) {
                    value_sort_by = VALUE_SORT_BY_POPULARITY;
                } else value_sort_by = VALUE_SORT_BY_RATING;
                // Build the URL
                Uri builtUri = Uri.parse(VALUE_BASE_URL).buildUpon()
                        .appendQueryParameter(PARAMETER_SORT_BY, value_sort_by)
                        .appendQueryParameter(PARAMETER_API_KEY, VALUE_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                rawData = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                // reformatMovieData() parses JSON and builds a String array
                // what we return here goes directly to onPostExecute()
                return reformatMovieData(rawData);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        private ArrayList<MovieObject> reformatMovieData(String rawInput)
                throws JSONException {

            // These are the JSON elements we need to extract
            final String MOVIE_RESULTS              = "results";

            final String MOVIE_POSTER_PATH          = "poster_path";
            final String MOVIE_ADULT                = "adult";
            final String MOVIE_OVERVIEW             = "overview";
            final String MOVIE_RELEASE_DATE         = "release_date";
            final String MOVIE_GENRE_IDS            = "genre_ids";
            final String MOVIE_ID                   = "id";
            final String MOVIE_ORIGINAL_TITLE       = "original_title";
            final String MOVIE_ORIGINAL_LANGUAGE    = "original_language";
            final String MOVIE_TITLE                = "title";
            final String MOVIE_BACKDROP_PATH        = "backdrop_path";
            final String MOVIE_POPULARITY           = "popularity";
            final String MOVIE_VOTE_COUNT           = "vote_count";
            final String MOVIE_VIDEO                = "video";
            final String MOVIE_VOTE_AVERAGE         = "vote_average";

            // Build a JSONObject from the rawInput. This is going to be multiple
            //entries at this point.
            JSONObject blobOfJSON = new JSONObject(rawInput);

            // Split the JSON object up into an array, Keyed on "results"
            JSONArray movieJSONArray = blobOfJSON.getJSONArray(MOVIE_RESULTS);

            //Create the ArrayList we'll need to return
            ArrayList<MovieObject> movies = new ArrayList<>();

            // Iterate through the JSON array
            for(int i=0;i<movieJSONArray.length();i++){
                // Build an object to look at this JSON
                JSONObject tempJSON = movieJSONArray.getJSONObject(i);
                // Build an empty MovieObject
                MovieObject tempMovie = new MovieObject();

                // Populate the MovieObject with the JSON data
                tempMovie.setMovie_poster_path(tempJSON.getString(MOVIE_POSTER_PATH));
                tempMovie.setMovie_adult(tempJSON.getString(MOVIE_ADULT));
                tempMovie.setMovie_overview(tempJSON.getString(MOVIE_OVERVIEW));
                tempMovie.setMovie_release_date(tempJSON.getString(MOVIE_RELEASE_DATE));
                tempMovie.setMovie_genre_ids(tempJSON.getString(MOVIE_GENRE_IDS));
                tempMovie.setMovie_id(tempJSON.getString(MOVIE_ID));
                tempMovie.setMovie_original_title(tempJSON.getString(MOVIE_ORIGINAL_TITLE));
                tempMovie.setMovie_original_language(tempJSON.getString(MOVIE_ORIGINAL_LANGUAGE));
                tempMovie.setMovie_title(tempJSON.getString(MOVIE_TITLE));
                tempMovie.setMovie_backdrop_path(tempJSON.getString(MOVIE_BACKDROP_PATH));
                tempMovie.setMovie_popularity(tempJSON.getString(MOVIE_POPULARITY));
                tempMovie.setMovie_vote_count(tempJSON.getString(MOVIE_VOTE_COUNT));
                tempMovie.setMovie_video(tempJSON.getString(MOVIE_VIDEO));
                tempMovie.setMovie_vote_average(tempJSON.getString(MOVIE_VOTE_AVERAGE));

                // Add the populated movie to the ArrayList we're going to return
                movies.add(tempMovie);
            }

            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieObject> movies) {
            theHopper.clear();

            if(movies!=null){

                for(MovieObject a : movies){
                    theHopper.add(a);
                }
            imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateMovies(String preferences){
        FetchMoviesTask fetch = new FetchMoviesTask();
        fetch.execute(preferences);
    }
}
