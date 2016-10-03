package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.AccessControlContext;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by robert on 10/2/16.
 */

public class InternetDownloadTask {

    final String VALUE_API_KEY = "";
    private Boolean SORT_BY_POPULARITY = true;
    private Boolean THRU_ALREADY = false;
    private final Context mContext;

    public InternetDownloadTask(Context context) {
        mContext = context;
    }

    public void updateMovies() {
        // Build a new AsyncTask
        FetchMoviesTask fetch = new FetchMoviesTask();
        // launch doInBackground, which will return to onPostExecute.
        fetch.execute();
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
            // Log.e("doInBackground()", builtUri.toString());


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
                ContentValues values = new ContentValues();
                // TMDB occasionally sends results with missing poster paths
                // Logging these as errors
                if (tempJSON.getString(MOVIE_POSTER_PATH).contains("null")) {
                    Log.e("NULL Poster Path: ", tempJSON.getString(MOVIE_POSTER_PATH));
                } else {
                    /*

                    This was fine when we were passing an arraylist of movie objects
                    to a bulkinsert method in our database manager, but we need to send
                    ContentValues to the Content Provider, so the assigment needs to change.

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

                    // Movie object is built, need to insert it via Content Provider here
                    // Still need to understand that part, so this is just a placeholder for now

                    */

                    values.put(TMDBContract.MovieEntry.MOVIE_POSTER_PATH,       tempJSON.getString(MOVIE_POSTER_PATH));
                    values.put(TMDBContract.MovieEntry.MOVIE_ADULT,             tempJSON.getString(MOVIE_ADULT).equals("true"));
                    values.put(TMDBContract.MovieEntry.MOVIE_OVERVIEW,          tempJSON.getString(MOVIE_OVERVIEW));
                    values.put(TMDBContract.MovieEntry.MOVIE_RELEASE_DATE,      tempJSON.getString(MOVIE_RELEASE_DATE));
                    values.put(TMDBContract.MovieEntry.MOVIE_GENRE_IDS,         tempJSON.getString(MOVIE_GENRE_IDS));
                    values.put(TMDBContract.MovieEntry.MOVIE_ID,                Integer.valueOf(tempJSON.getString(MOVIE_ID)));
                    values.put(TMDBContract.MovieEntry.MOVIE_ORIGINAL_TITLE,    tempJSON.getString(MOVIE_ORIGINAL_TITLE));
                    values.put(TMDBContract.MovieEntry.MOVIE_ORIGINAL_LANGUAGE, tempJSON.getString(MOVIE_ORIGINAL_LANGUAGE));
                    values.put(TMDBContract.MovieEntry.MOVIE_TITLE,             tempJSON.getString(MOVIE_TITLE));
                    values.put(TMDBContract.MovieEntry.MOVIE_BACKDROP_PATH,     tempJSON.getString(MOVIE_BACKDROP_PATH));
                    values.put(TMDBContract.MovieEntry.MOVIE_POPULARITY,        Float.parseFloat(tempJSON.getString(MOVIE_POPULARITY)));
                    values.put(TMDBContract.MovieEntry.MOVIE_VOTE_COUNT,        Long.parseLong(tempJSON.getString(MOVIE_VOTE_COUNT)));
                    values.put(TMDBContract.MovieEntry.MOVIE_VIDEO,             tempJSON.getString(MOVIE_VIDEO).equals("true"));
                    values.put(TMDBContract.MovieEntry.MOVIE_VOTE_AVERAGE,      Float.parseFloat(tempJSON.getString(MOVIE_VOTE_AVERAGE)));

                    Log.e("MainActivity", "Content values in place, calling contentprovider insert");

                    Uri insertedUri = mContext.getContentResolver().insert(
                            TMDBContract.MovieEntry.CONTENT_URI,
                            values
                    );

                    values.clear();
                    if (SORT_BY_POPULARITY) {
                        values.put(TMDBContract.MovieEntry.MOVIE_MOST_POPULAR, true);
                        // Call the db update here
                    } else {
                        values.put(TMDBContract.MovieEntry.MOVIE_TOP_RATED, true);
                        // Call the db update here;
                    }

                    // movies.add(tempMovie);
                }
            }

            // This will all go away as the CP will handle DB access.
            /* openDB();
            database.bulkInsert(movies);
            closeDB();*/

        }

        @Override
        protected void onPostExecute(Boolean dibResult) {

            if (dibResult) {
                if (!THRU_ALREADY) {
                    THRU_ALREADY = true;
                    SORT_BY_POPULARITY = false;
                    // Log.e("onPostExecute", "First Time Through");
                    updateMovies();
                } else {
                    // Log.e("onPostExecute", "Second Time Through");
                }
            } else {
                Log.e("onPostExecute():","doInBackground did not return true");
            }
        }
    }
}