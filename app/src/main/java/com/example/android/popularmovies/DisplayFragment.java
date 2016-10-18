
/* DisplayFragment exists to display detail information about a specific movie.
 * It's launched by intent, and the intent includes a ArrayList.
 *
 * The ArrayList is picked apart, and the elements are used to populate the
 * views in the layout.
 */

package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
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

import static android.media.CamcorderProfile.get;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_BACKDROP_PATH;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_GENRE_IDS;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_ID;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_ORIGINAL_LANGUAGE;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_ORIGINAL_TITLE;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_OVERVIEW;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_POPULARITY;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_POSTER_PATH;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_RELEASE_DATE;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_TITLE;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_VIDEO;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_VOTE_AVERAGE;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_VOTE_COUNT;

public class DisplayFragment extends Fragment {
    // The arraylist of extras passed with the intent is called "movie_details"
    public final static String MOVIE_DETAILS = "movie_details";

    // This is the base path for all images.
    final String baseurl = "http://image.tmdb.org/t/p/w780";

    Context context;
    ArrayList<DisplayExtras> extras;
    ListViewArrayAdapter adapter;
    String LOG = "GridFragment";

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.display_fragment, container, false);

        context = getActivity();
        Cursor cursor;

        // Look in the database to identify the last movie the user selected
        cursor = getActivity().getContentResolver().query(
                TMDBContract.buildUserSelectionURI(),
                null,
                null,
                null,
                TMDBContract.UserMetrics.COLUMN_UID + " DESC LIMIT 1");

        long moviequery = 0;

        if (cursor.moveToFirst()) {
            // Look in the UserMetrics table - what was the last movie the user selected?
            moviequery = Long.parseLong((cursor.getString(cursor.getColumnIndex(TMDBContract.UserMetrics.COLUMN_SELECTED_MOVIE))));
        } else {
            // Nothing in the UserMetrics table? Point to the first movie.
            moviequery = 1;
        }
        cursor.close();

        // Pull the details on the selected movie from the MovieEntry table
        cursor = getActivity().getContentResolver().query(
                TMDBContract.buildMovieURI(moviequery),
                TMDBContract.MovieEntry.MOVIE_ALL_KEYS,
                null,
                null,
                null);

        // Populate the movie object
        final MovieObject movie = new MovieObject();

        if (cursor.moveToFirst()) {
            movie.setMovie_poster_path(cursor.getString(cursor.getColumnIndex(MOVIE_POSTER_PATH)));
            movie.setMovie_overview(cursor.getString(cursor.getColumnIndex(MOVIE_OVERVIEW)));
            movie.setMovie_release_date(cursor.getString(cursor.getColumnIndex(MOVIE_RELEASE_DATE)));
            movie.setMovie_genre_ids(cursor.getString(cursor.getColumnIndex(MOVIE_GENRE_IDS)));
            movie.setMovie_original_title(cursor.getString(cursor.getColumnIndex(MOVIE_ORIGINAL_TITLE)));
            movie.setMovie_original_language(cursor.getString(cursor.getColumnIndex(MOVIE_ORIGINAL_LANGUAGE)));
            movie.setMovie_title(cursor.getString(cursor.getColumnIndex(MOVIE_TITLE)));
            movie.setMovie_id(cursor.getString(cursor.getColumnIndex(MOVIE_ID)));
            movie.setMovie_backdrop_path(cursor.getString(cursor.getColumnIndex(MOVIE_BACKDROP_PATH)));
            movie.setMovie_popularity(Float.parseFloat(cursor.getString(cursor.getColumnIndex(MOVIE_POPULARITY))));
            movie.setMovie_vote_count(Long.parseLong(cursor.getString(cursor.getColumnIndex(MOVIE_VOTE_COUNT))));
            movie.setMovie_video(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(MOVIE_VIDEO))));
            movie.setMovie_vote_average(Float.parseFloat(cursor.getString(cursor.getColumnIndex(MOVIE_VOTE_AVERAGE))));
            movie.setMovie_top_rated(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_TOP_RATED)));
            movie.setMovie_most_popular(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_MOST_POPULAR)));
            movie.setMovie_favorite(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_USER_FAVORITE)));

        } else {
            // Load it up with dummy information if you don't find anything in the MovieEntry table
            // This happens the first time the app is fired up, as it hasn't been populated yet.
            Log.e("DisplayFragment", "Cursor returned no rows");
            movie.setMovie_poster_path("junk");
            movie.setMovie_overview("This is the best Popular Movies Application, ever.");
            movie.setMovie_release_date("Version 1 - October 2016");
            movie.setMovie_title("Welcome to Popular Movies!");
            movie.setMovie_id("-1");
            movie.setMovie_vote_average(5);
            movie.setMovie_favorite("true");

        }
        cursor.close();

        // reference to the poster
        ImageView poster = (ImageView) rootView.findViewById(R.id.posterpath);

        // Load poster image
        Picasso.with(getActivity())
                .load(baseurl.concat(movie.getMovie_poster_path()))
                .placeholder(R.drawable.blank)
                .into(poster);

        // reference to the imageView, which I'm using as a button
        final ImageView LikeButton = (ImageView) rootView.findViewById(R.id.like_button);

        // image we'll display depends upon whether the user likes this selection
        int likeImage = R.drawable.mightlike;
        if (movie.getMovie_favorite().equals("true")){
            likeImage = R.drawable.dolike;
        }
        LikeButton.setImageResource(likeImage);

        // Listen for clicks on the LikeButton imageview
        LikeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Read Status
                ContentValues values = new ContentValues();
                values.clear();
                Communicator communicator = (Communicator) getActivity();
                if(movie.getMovie_favorite().equals("false")){
                    movie.setMovie_favorite("true");
                    values.put(TMDBContract.MovieEntry.MOVIE_USER_FAVORITE, "true");                    // Update Image with true image
                    Toast.makeText(getActivity(), "Added to Favorites",
                            Toast.LENGTH_LONG).show();
                    LikeButton.setImageResource(R.drawable.dolike);
                    communicator.likeButton();
                } else {
                    movie.setMovie_favorite("false");
                    values.put(TMDBContract.MovieEntry.MOVIE_USER_FAVORITE, "false");                    // Update Image with false image
                    Toast.makeText(getActivity(), "Removed from Favorites",
                            Toast.LENGTH_LONG).show();
                    LikeButton.setImageResource(R.drawable.mightlike);
                    communicator.likeButton();
                }
                // Update database
                if (Long.parseLong(movie.getMovie_id()) > -1){
                    int response = context.getContentResolver().update(TMDBContract.buildMovieURI(Long.parseLong(movie.getMovie_id())),
                            values,
                            TMDBContract.MovieEntry.MOVIE_ID + " = ?",
                            new String[]{movie.getMovie_id()});
                }
            }
        });

        // Reference to titleBar and assignment.
        TextView titleBar = (TextView) rootView.findViewById(R.id.title_bar);
        titleBar.setText(movie.getMovie_title());

        // reference to releaseDate and assignment
        TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
        releaseDate.setText(movie.getMovie_release_date());

        // reference to summaryText and assignment
        TextView summaryText = (TextView) rootView.findViewById(R.id.summary_text);
        summaryText.setText(movie.getMovie_overview());

        // reference to ratingBar and assignment.
        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        float rating = Float.parseFloat(String.valueOf(movie.getMovie_vote_average()));

        // rating bar is on a 5 point scale, database is on a 10 point scale. Adjusting...
        ratingBar.setRating((float) (rating/2.0));

        // get the movieid from the object.
        long movieid = Long.parseLong(movie.getMovie_id());

        // Place for the reviews and trailers to sit
        extras = new ArrayList<>();

        // Way to bind movies and trailers to a listview
        adapter = new ListViewArrayAdapter(getActivity(), R.id.display_list_view, extras);

        // reference to the listview
        final ListView listView = (ListView) rootView.findViewById(R.id.display_list_view);

        // set adapter on listview
        listView.setAdapter(adapter);

        // Now that we're all setup, let's get the trailers and reviews
        //Build Async Task
        GetMovieExtras getsome = new GetMovieExtras();

        // Tell the Async Task to get Trailers for this movie
        getsome.setBuiltUri(buildURI(1, movieid));

        // Go! Async task will handle parsing and dropping into ArrayList
        getsome.execute();

        // Rinse and repeat for trailers
        GetMovieExtras getmore = new GetMovieExtras();
        getmore.setBuiltUri(buildURI(2, movieid));
        getmore.execute();

        // Listen for clicks on reviews and trailers
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Grab the URL associated with the user selection
                String location = extras.get(i).getLocation();

                // Fire up a new intent
                Intent intent = new Intent(Intent.ACTION_VIEW);

                // Add the URI to the intent
                intent.setData(Uri.parse(location));

                // Fire!
                startActivity(intent);
            }
        });

        return rootView;
    }

    private Uri buildURI(int type, long movieID){
        final String VALUE_BASE_URL = "https://api.themoviedb.org/3/movie/";


        final String PARAMETER_API_KEY = "api_key";
        final int VIDEOS = 1;
        final int REVIEWS = 2;
        String webRequest = null;


        // finalize the search /movie/{id}/videos
        if (type == VIDEOS) {
            webRequest = VALUE_BASE_URL + movieID + "/videos";
        } else {
            webRequest = VALUE_BASE_URL + movieID + "/reviews";
        }
        // Build the URL
        return Uri.parse(webRequest).buildUpon()
                .appendQueryParameter(PARAMETER_API_KEY, TMDBContract.API_KEY)
                .build();
    }

    private class GetMovieExtras extends AsyncTask<String, Void, String> {

        Uri builtUri;

        public void setBuiltUri(Uri uri){
            builtUri = uri;
        }

        // this is the .execute of the Async task. Returns hit onPostExecute()
        @Override
        protected String doInBackground(String... strings) {
            // Go get the data
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
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
                String rawData = buffer.toString();

                // This information gets sent to onPostExecute()
                return rawData;

            } catch (IOException e) {
                // drop an error in the log if something goes wrong
                Log.e(LOG, "doInBackground() Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        // drop an error in the log if something goes wrong
                        Log.e(LOG, "doInBackground() Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // if we received data, parse it.
            if (s != null) {
                try {
                    // take raw data (s), send it to parseJSONdata along with the type (videos or reviews, from URI)
                    // add the returned arraylist to extras
                    extras.addAll(parseJSONdata(s, builtUri.getPathSegments().get(3)));
                    // Let the adapter know there's new data
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.e("DisplayFragment", "No data onPostExecute - may be first run.");
            }
        }
    }

   private ArrayList<DisplayExtras> parseJSONdata(String rawInput, String type)
            throws JSONException {

        // These are the JSON elements we need to extract
        final String RESULTS = "results";
        final String URL = "url";
        final String KEY = "key";

       // Build a JSONObject from the rawInput. This is going to be multiple
       //entries at this point.
       JSONObject blobOfJSON = new JSONObject(rawInput);

       // Split the JSON object up into an array, Keyed on "results"
       JSONArray movieJSONArray = blobOfJSON.getJSONArray(RESULTS);

       // ArrayList to hold all of the extracted movie reviews and trailers
       ArrayList<DisplayExtras> extras = new ArrayList<>();

       // Iterate through the JSON array
       for (int i = 0; i < movieJSONArray.length(); i++) {
            // Build an object to look at this JSON
            JSONObject tempJSON = movieJSONArray.getJSONObject(i);
            // Build a DisplayExtras object to hold the extracted data
            DisplayExtras displayExtras = new DisplayExtras();
            displayExtras.setType(type);
            if (type.equals("videos")){
                displayExtras.setLocation("https://www.youtube.com/watch?v=" + tempJSON.getString(KEY));
            }
           else {
                displayExtras.setLocation(tempJSON.getString(URL));
            }
            extras.add(displayExtras);
        }
       // send back the populated arraylist
       return extras;
   }
}
