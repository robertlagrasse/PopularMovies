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
/* DisplayActivity exists to display detail information about a specific movie.
 * It's launched by intent, and the intent includes a ArrayList.
 *
 * The ArrayList is picked apart, and the elements are used to populate the
 * views in the layout.
 */

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
        Log.e(LOG, "onResume");
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG, "onCreateView");
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
            moviequery = Long.parseLong((cursor.getString(cursor.getColumnIndex(TMDBContract.UserMetrics.COLUMN_SELECTED_MOVIE))));
            Log.e("SELECTED_MOVIE:", (cursor.getString(cursor.getColumnIndex(TMDBContract.UserMetrics.COLUMN_SELECTED_MOVIE))));
        } else {
            Log.e("DisplayFragment", "Cursor returned no rows");
            moviequery = 1;
        }
        cursor.close();

        // Pull the details on the selected movie
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
            Log.e("DisplayFragment", "Cursor returned no rows");
        }
        cursor.close();

        ImageView poster = (ImageView) rootView.findViewById(R.id.posterpath);

        Picasso.with(getActivity())
                .load(baseurl.concat(movie.getMovie_poster_path()))
                .into(poster);

        Log.e("Poster",baseurl.concat(movie.getMovie_poster_path()));

        final ImageView LikeButton = (ImageView) rootView.findViewById(R.id.like_button);
        int likeImage = R.drawable.mightlike;
        if (movie.getMovie_favorite().equals("true")){
            likeImage = R.drawable.dolike;
        }
        LikeButton.setImageResource(likeImage);

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
                int response = context.getContentResolver().update(TMDBContract.buildMovieURI(Long.parseLong(movie.getMovie_id())),
                        values,
                        TMDBContract.MovieEntry.MOVIE_ID + " = ?",
                        new String[]{movie.getMovie_id()});
            }
        });

        TextView titleBar = (TextView) rootView.findViewById(R.id.title_bar);

        titleBar.setText(movie.getMovie_title());

        TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
        releaseDate.setText(movie.getMovie_release_date());

        TextView summaryText = (TextView) rootView.findViewById(R.id.summary_text);
        summaryText.setText(movie.getMovie_overview());

        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        float rating = Float.parseFloat(String.valueOf(movie.getMovie_vote_average()));
        ratingBar.setRating((float) (rating/2.0));

        Log.e("moviequery", "sending " +moviequery);
        long movieid = Long.parseLong(movie.getMovie_id());

        // Grab any reviews or trailers
        extras = new ArrayList<>();
        adapter = new ListViewArrayAdapter(getActivity(), R.id.display_list_view, extras);
        final ListView listView = (ListView) rootView.findViewById(R.id.display_list_view);
        listView.setAdapter(adapter);
        GetMovieExtras getsome = new GetMovieExtras();
        getsome.setBuiltUri(buildURI(1, movieid));
        getsome.execute();
        // Parse
        // Drop into ArrayList<DisplayExtras>
        GetMovieExtras getmore = new GetMovieExtras();
        getmore.setBuiltUri(buildURI(2, movieid));
        getmore.execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String location = extras.get(i).getLocation();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(location));
                startActivity(intent);
            }
        });

        return rootView;
    }

    private Uri buildURI(int type, long movieID){
        Log.e(LOG, "buildURI");
        final String VALUE_BASE_URL = "https://api.themoviedb.org/3/movie/";


        final String PARAMETER_SORT_BY = "sort_by";
        final String VALUE_SORT_BY_POPULARITY = "popular";
        final String VALUE_SORT_BY_RATING = "top_rated";

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

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Log.e(LOG, "doInBackground");
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
                // Log.e("doInBackground", builtUri.getPathSegments().get(3));
                String rawData = buffer.toString();
                // Log.e("Download",rawData);
                return rawData;

            } catch (IOException e) {
                Log.e("doInBackground()", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
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
                        Log.e("doInBackground()", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                extras.addAll(parseJSONdata(s, builtUri.getPathSegments().get(3)));
                adapter.notifyDataSetChanged();
                Log.e("onPostExecute", "extras.size()" + extras.size());
            }
            catch (JSONException e) {
                e.printStackTrace();
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

       Log.e("JSONParser", "JSONArray.length()" + movieJSONArray.length());

       // ArrayList to hold all of the extracted movies
       ArrayList<DisplayExtras> extras = new ArrayList<>();

       // Iterate through the JSON array
       for (int i = 0; i < movieJSONArray.length(); i++) {
            // Build an object to look at this JSON
            JSONObject tempJSON = movieJSONArray.getJSONObject(i);
            // Build a CV object to hold the extracted data
            DisplayExtras displayExtras = new DisplayExtras();
            displayExtras.setType(type);
            if (type.equals("videos")){
                displayExtras.setLocation("https://www.youtube.com/watch?v=" + tempJSON.getString(KEY));
                Log.e("PARSER","Matched VIDEOS " + displayExtras.getLocation());
            }
           else {
                displayExtras.setLocation(tempJSON.getString(URL));
                Log.e("PARSER","Matched Reviews " + displayExtras.getLocation());
            }
            extras.add(displayExtras);
        }
       Log.e("PARSER","EXITED LOOP");
       return extras;
   }
}
