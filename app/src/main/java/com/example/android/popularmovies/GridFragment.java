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

    public Boolean SORT_BY_POPULARITY = true;

    private Boolean THRU_ALREADY = false;

    // Standing up the database instance at a fragement level for now.
    private DBManager database;

    @Override
    public void onResume() {
        super.onResume();
        // Update whenever the fragment resumes.
        THRU_ALREADY = false;
        updateMovies();
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
        protected ArrayList<MovieObject> doInBackground(String... strings) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String rawData = null;



            try {
                // Constants to identify parameters and corresponding values
                // for use in the construction of the URL

                final String VALUE_BASE_URL                     =   "https://api.themoviedb.org/3/movie/";

                final String PARAMETER_SORT_BY                  =   "sort_by";
                final String VALUE_SORT_BY_POPULARITY           =   "popular";
                final String VALUE_SORT_BY_RATING               =   "top_rated";

                final String PARAMETER_API_KEY                  =   "api_key";
                final String VALUE_API_KEY                      =   "f42ec8a4b30bcaf191a165668a819fda";

                // Grab user preferences
                SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sortBy = userPreferences.getString(getString(R.string.preferences_key_sort_by),getString(R.string.preferences_entryValue_sort_by_rating));

                // Use the preferences to make some decisions about the way we search
                String webRequest = null;

                Log.e(LOG_TAG, SORT_BY_POPULARITY.toString());

                if (SORT_BY_POPULARITY)
                {
                    webRequest = VALUE_BASE_URL + VALUE_SORT_BY_POPULARITY;
                    }
                else {
                     webRequest = VALUE_BASE_URL + VALUE_SORT_BY_RATING;
                }

                Log.e(LOG_TAG, webRequest);
                // Build the URL
                Uri builtUri = Uri.parse(webRequest).buildUpon()
                        .appendQueryParameter(PARAMETER_API_KEY, VALUE_API_KEY)
                        .build();
                Log.e(LOG_TAG, builtUri.toString());

                URL url = new URL(builtUri.toString());

                // Create the request, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                // If everything worked out with the connection, dump the buffer into rawData
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
            for(int i=0;i<movieJSONArray.length();i++) {
                // Build an object to look at this JSON
                JSONObject tempJSON = movieJSONArray.getJSONObject(i);
                // Build an empty MovieObject
                MovieObject tempMovie = new MovieObject();

                // TMDB occasionally sends results with missing poster paths
                // Logging these as errors
                if (tempJSON.getString(MOVIE_POSTER_PATH).contains("null")) {
                    Log.e(LOG_TAG, tempJSON.getString(MOVIE_TITLE) + " contained null poster path.");
                    Log.e("Poster Path: ", tempJSON.getString(MOVIE_POSTER_PATH));
                } else {
                    // If there is a valid poster path, then
                    // Populate the MovieObject with the JSON data
                    tempMovie.setMovie_poster_path(tempJSON.getString(MOVIE_POSTER_PATH));
                    tempMovie.setMovie_adult(tempJSON.getString(MOVIE_ADULT).equals("true"));
                    tempMovie.setMovie_overview(tempJSON.getString(MOVIE_OVERVIEW));
                    tempMovie.setMovie_release_date(tempJSON.getString(MOVIE_RELEASE_DATE));
                    tempMovie.setMovie_genre_ids(tempJSON.getString(MOVIE_GENRE_IDS));
                    tempMovie.setMovie_id(Integer.valueOf(tempJSON.getString(MOVIE_ID)));
                    tempMovie.setMovie_original_title(tempJSON.getString(MOVIE_ORIGINAL_TITLE));
                    tempMovie.setMovie_original_language(tempJSON.getString(MOVIE_ORIGINAL_LANGUAGE));
                    tempMovie.setMovie_title(tempJSON.getString(MOVIE_TITLE));
                    tempMovie.setMovie_backdrop_path(tempJSON.getString(MOVIE_BACKDROP_PATH));
                    tempMovie.setMovie_popularity(Float.parseFloat(tempJSON.getString(MOVIE_POPULARITY)));
                    tempMovie.setMovie_vote_count(Long.parseLong(tempJSON.getString(MOVIE_VOTE_COUNT)));
                    tempMovie.setMovie_video(tempJSON.getString(MOVIE_VIDEO).equals("true"));
                    tempMovie.setMovie_vote_average(Float.parseFloat(tempJSON.getString(MOVIE_VOTE_AVERAGE)));

                    // label the movie object with its sort origin. Was this popular or top rated?
                    if (SORT_BY_POPULARITY){
                        tempMovie.setMovie_result_type("popular");
                    }
                    else tempMovie.setMovie_result_type("top_rated");

                    // Add the populated movie to the ArrayList we're going to return
                    movies.add(tempMovie);
                }
            }

            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieObject> movies) {
            // theHopper.clear();
            if(movies!=null){
                for(MovieObject a : movies){
                    theHopper.add(a);
                }
            imageAdapter.notifyDataSetChanged();
            }
            if (!THRU_ALREADY) {
                THRU_ALREADY = true;
                SORT_BY_POPULARITY = false;
                updateMovies();
            } else{
                Log.e("PostExecute", "database = new DBManager(getActivity());");
                database = new DBManager(getActivity());
                Log.e("PostExecute", "database.bulkInsert(theHopper);");
                database.bulkInsert(theHopper);
                Log.e("onPostExecute", "Count: " + database.getDBCount());
                database.close();
            }
        }
    }

    private void updateMovies(){
        // Build a new AsyncTask
        FetchMoviesTask fetch = new FetchMoviesTask();
        // launch doInBackground, which will return to onPostExecute.
        fetch.execute();
    }
}
