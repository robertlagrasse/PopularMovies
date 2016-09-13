package com.example.android.popularmovies;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * TMDBQueryManagerFragment - retrieve and format data from TMDB
 *
 * Requires: Internet Access
 *
 * TODO: JSON Formatting
 * TODO: Build an interface to return information to calling activity.
 *
 */

public class TMDBQueryManagerFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        FetchMoviesTask fetch = new FetchMoviesTask();
        fetch.execute("Shaft!");
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        // doInBackground()
        // This method is called when an AsyncTask object calls its
        // .execute() method.
        //
        @Override
        protected String doInBackground(String... strings) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String rawData = null;

            try {
                // Constants to identify parameters and corresponding values
                // for use in the construction of the URL

                final String PARAMETER_BASE_URL     =   null;
                final String VALUE_BASE_URL         =   "https://api.themoviedb.org/3/movie/550?";

                final String PARAMETER_API_KEY      =   "api_key";
                final String VALUE_API_KEY          =   "f42ec8a4b30bcaf191a165668a819fda";

                // Build the URL
                Uri builtUri = Uri.parse(VALUE_BASE_URL).buildUpon()
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
                // Send rawData to be reformatted before returning
                // This should be what happens if everything goes as planned.
                // This return actually directs to onPostExecute()
                return reformatMovieData(rawData);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        // This method takes raw Json input and extracts values
        // It's just a placeholder for now
        private String reformatMovieData(String rawInput)
                throws JSONException {

            // These are the JSON elements we need to extract

            final String POPULARTIY = "popularity";
            final String POSTER_PATH = "poster_path";
            final String RELEASE_DATE = "release_date";
            final String TITLE = "title";
            final String VOTE_AVERAGE = "vote_average";

            String extracted = null;

            JSONObject movieJSON = new JSONObject(rawInput);

            Log.e("reformatMovieData:", POPULARTIY + " : " + movieJSON.getString(POPULARTIY));
            Log.e("reformatMovieData:", POSTER_PATH + " : " + movieJSON.getString(POSTER_PATH));
            Log.e("reformatMovieData:", RELEASE_DATE + " : " + movieJSON.getString(RELEASE_DATE));
            Log.e("reformatMovieData:", TITLE + " : " + movieJSON.getString(TITLE));
            Log.e("reformatMovieData:", VOTE_AVERAGE + " : " + movieJSON.getString(VOTE_AVERAGE));

            return rawInput;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("onPostExecute", result);






        }
    }

}
