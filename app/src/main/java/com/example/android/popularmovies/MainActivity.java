package com.example.android.popularmovies;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

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

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class MainActivity extends AppCompatActivity implements Communicator {
    public final static String MOVIE_DETAILS = "movie_details";
    public Boolean SORT_BY_POPULARITY = true;
    public Boolean THRU_ALREADY = false;
    DatabaseAdapter database;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("onCreate()", "Calling updateMovies()");
        updateMovies();

        // When the application fires up, set the default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Create a FragmentManager and start a fragmentTransaction
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create an instance of GridFragment and drop it on activity_main
        GridFragment gridFragment = new GridFragment();
        fragmentTransaction.add(R.id.activity_main, gridFragment);
        fragmentTransaction.commit();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public void respond(ArrayList<String> data) {

        // An ArrayList called data has arrived. It needs to be passed via Intent
        // to DisplayActivity.

        // First, create the intent
        Intent showMovieDetails = new Intent(this, DisplayActivity.class);

        // Next, attach the data to the intent
        showMovieDetails.putExtra(MOVIE_DETAILS, data);

        // Finally, start the activity
        startActivity(showMovieDetails);
    }

    private void updateMovies() {
        // Build a new AsyncTask
        FetchMoviesTask fetch = new FetchMoviesTask();
        // launch doInBackground, which will return to onPostExecute.
        fetch.execute();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... strings) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String rawData = null;

            // Constants to identify parameters and corresponding values
            // for use in the construction of the URL
            final String VALUE_BASE_URL = "https://api.themoviedb.org/3/movie/";

            final String PARAMETER_SORT_BY = "sort_by";
            final String VALUE_SORT_BY_POPULARITY = "popular";
            final String VALUE_SORT_BY_RATING = "top_rated";

            final String PARAMETER_API_KEY = "api_key";
            final String VALUE_API_KEY = "";
            String webRequest = null;

            if (SORT_BY_POPULARITY) {
                webRequest = VALUE_BASE_URL + VALUE_SORT_BY_POPULARITY;
            } else {
                webRequest = VALUE_BASE_URL + VALUE_SORT_BY_RATING;
            }

            // Build the URL
            Uri builtUri = Uri.parse(webRequest).buildUpon()
                    .appendQueryParameter(PARAMETER_API_KEY, VALUE_API_KEY)
                    .build();
            Log.e("doInBackground()", builtUri.toString());


            try {

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
                    return false;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return false;
                }
                // If everything worked out with the connection, dump the buffer into rawData
                rawData = buffer.toString();
                try {
                    reformatMovieData(rawData);
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("doInBackground()", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return false;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("doInBackground()", "Error closing stream", e);
                    }
                }
            }
            // Should never get here
            return false;
        }

        // This won't return anything anymore. We're just writing to the db
        private void reformatMovieData(String rawInput)
                throws JSONException {

            // These are the JSON elements we need to extract
            final String MOVIE_RESULTS = "results";

            final String MOVIE_POSTER_PATH = "poster_path";
            final String MOVIE_ADULT = "adult";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_RELEASE_DATE = "release_date";
            final String MOVIE_GENRE_IDS = "genre_ids";
            final String MOVIE_ID = "id";
            final String MOVIE_ORIGINAL_TITLE = "original_title";
            final String MOVIE_ORIGINAL_LANGUAGE = "original_language";
            final String MOVIE_TITLE = "title";
            final String MOVIE_BACKDROP_PATH = "backdrop_path";
            final String MOVIE_POPULARITY = "popularity";
            final String MOVIE_VOTE_COUNT = "vote_count";
            final String MOVIE_VIDEO = "video";
            final String MOVIE_VOTE_AVERAGE = "vote_average";

            // Build a JSONObject from the rawInput. This is going to be multiple
            //entries at this point.
            JSONObject blobOfJSON = new JSONObject(rawInput);

            // Split the JSON object up into an array, Keyed on "results"
            JSONArray movieJSONArray = blobOfJSON.getJSONArray(MOVIE_RESULTS);

            // ArrayList to hold all of the extracted movies
            ArrayList<MovieObject> movies = new ArrayList<>();

            // TODO: Tighten this up. Reuse same objects?
            // Iterate through the JSON array
            for (int i = 0; i < movieJSONArray.length(); i++) {
                // Build an object to look at this JSON
                JSONObject tempJSON = movieJSONArray.getJSONObject(i);
                // Build an empty MovieObject
                MovieObject tempMovie = new MovieObject();

                // TMDB occasionally sends results with missing poster paths
                // Logging these as errors
                if (tempJSON.getString(MOVIE_POSTER_PATH).contains("null")) {
                    Log.e("NULL Poster Path: ", tempJSON.getString(MOVIE_POSTER_PATH));
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
                    if (SORT_BY_POPULARITY) {
                        tempMovie.setMovie_result_type("popular");
                    } else tempMovie.setMovie_result_type("top_rated");

                    // Add the populated movie to the ArrayList we're going to return
                    // This get's replaced by a call to the db.
                    movies.add(tempMovie);

                }
            }
            openDB();
            database.bulkInsert(movies);
            closeDB();

        }

        @Override
        protected void onPostExecute(Boolean dibResult) {
            // This will just be the search loop and a notifier.
            if (dibResult){
                Log.e("onPostExecute", "Boolean True");
            } else
            {
                Log.e("onPostExecute", "Boolean False");
            }
            if (!THRU_ALREADY) {
                THRU_ALREADY = true;
                SORT_BY_POPULARITY = false;
                Log.e("onPostExecute", "First Time Through");
                updateMovies();
            } else {
                Log.e("onPostExecute", "Second Time Through");
            }
        }
    }

    private void openDB() {
        database = new DatabaseAdapter(this);
        database.open();
    }

    private void closeDB() {
        database.close();
    }
}



