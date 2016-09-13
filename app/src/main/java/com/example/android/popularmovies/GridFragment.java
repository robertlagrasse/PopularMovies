package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by robert on 9/10/16.
 * This fragment will present a gridview to the user, and use an interface to communicate
 * the user's choice back to the calling activity.
 */
public class GridFragment extends Fragment {

    // Interface reference variable
    Communicator communicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.grid_fragment, container, false);

        // Point my Interface reference variable at the activity that spawned this fragment.
        communicator = (Communicator) getActivity();

        // Build object to fetch movie data
        FetchMoviesTask fetch = new FetchMoviesTask();
        // Launch the AsyncTask's doInBackground
        // The AsyncTask will download the data and run it through a JSONParser
        // doInBackground will then return, which kicks off AsyncTask onPostExecute().
        // onPostExecute populates the

        fetch.execute("Shaft!");

        // inflate the gridview, set ImageAdapter on it to populate with some dummy images
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getActivity()));

        // Listen for user interaction.
        // For now, drop something in the logs.
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                Log.e("GridFragment()", "Item Clicked at Position: " + position);
                // use the interface to pass position data to the respond method in the
                // activity the interface reference variable points to.
                communicator.respond(position);
            }
        });

        return rootView;
    }

    // adapted from https://developer.android.com/guide/topics/ui/layout/gridview.html
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(240, 240));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7
        };
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
                final String VALUE_BASE_URL         =   "https://api.themoviedb.org/3/discover/movie?";

                final String PARAMETER_SORT_BY      =   "sort_by";
                final String VALUE_SORT_BY          =   "popularity.desc";
                final String PARAMETER_API_KEY      =   "api_key";
                final String VALUE_API_KEY          =   "f42ec8a4b30bcaf191a165668a819fda";

                // Build the URL
                Uri builtUri = Uri.parse(VALUE_BASE_URL).buildUpon()
                        .appendQueryParameter(PARAMETER_SORT_BY, VALUE_SORT_BY)
                        .appendQueryParameter(PARAMETER_API_KEY, VALUE_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.e("TMDB-QMF", builtUri.toString());

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
                Log.e("TMDB-QMF", "reformatMovieData(" + rawData + ")");
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

            final String POPULARTIY         = "popularity";
            final String POSTER_PATH        = "poster_path";
            final String RELEASE_DATE       = "release_date";
            final String TITLE              = "title";
            final String VOTE_AVERAGE       = "vote_average";

            JSONObject movieJSON = new JSONObject(rawInput);
            JSONArray movieArray = movieJSON.getJSONArray("results");

            String[] extracted = new String[movieArray.length()];
            for(int i = 0; i < movieArray.length(); i++) {

                JSONObject movie = movieArray.getJSONObject(i);

                Log.e("movieArray", "element: " + i);
                Log.e("movieArray", POPULARTIY + movie.getString(POPULARTIY));
                Log.e("movieArray", POSTER_PATH + movie.getString(POSTER_PATH));
                Log.e("movieArray", RELEASE_DATE + movie.getString(RELEASE_DATE));
                Log.e("movieArray", TITLE + movie.getString(TITLE));
                Log.e("movieArray", VOTE_AVERAGE + movie.getString(VOTE_AVERAGE));
            }

            return rawInput;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("onPostExecute", result);






        }
    }

}
