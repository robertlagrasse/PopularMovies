package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_BACKDROP_PATH;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_OVERVIEW;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_POSTER_PATH;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_RELEASE_DATE;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_TITLE;
import static com.example.android.popularmovies.TMDBContract.MovieEntry.MOVIE_VOTE_AVERAGE;
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

    private long passedMovie;
    Context context;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View rootView = inflater.inflate(R.layout.display_activity, container, false);

        context = getActivity();
        Cursor cursor;
        cursor = getActivity().getContentResolver().query(
                TMDBContract.MovieEntry.buildMovieURI(passedMovie),
                TMDBContract.MovieEntry.MOVIE_ALL_KEYS,
                null,
                null,
                null);

        MovieObject movie = new MovieObject();
        if (cursor.moveToFirst()) {
            movie.setMovie_poster_path(cursor.getString(cursor.getColumnIndex(MOVIE_POSTER_PATH)));
            movie.setMovie_overview(cursor.getString(cursor.getColumnIndex(MOVIE_OVERVIEW)));
            movie.setMovie_release_date(cursor.getString(cursor.getColumnIndex(MOVIE_RELEASE_DATE)));
            movie.setMovie_genre_ids(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_GENRE_IDS)));
            movie.setMovie_original_title(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_ORIGINAL_TITLE)));
            movie.setMovie_original_language(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_ORIGINAL_LANGUAGE)));
            movie.setMovie_title(cursor.getString(cursor.getColumnIndex(MOVIE_TITLE)));
            movie.setMovie_backdrop_path(cursor.getString(cursor.getColumnIndex(MOVIE_BACKDROP_PATH)));
            movie.setMovie_popularity(Float.parseFloat(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_POPULARITY))));
            movie.setMovie_vote_count(Long.parseLong(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_VOTE_COUNT))));
            movie.setMovie_video(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_VIDEO))));
            movie.setMovie_vote_average(Float.parseFloat(cursor.getString(cursor.getColumnIndex(MOVIE_VOTE_AVERAGE))));
            movie.setMovie_top_rated(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_TOP_RATED)));
            movie.setMovie_most_popular(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_MOST_POPULAR)));
            movie.setMovie_favorite(cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_USER_FAVORITE)));

        } else {
            Log.e("DisplayFragment", "Cursor returned no rows");
        }
        cursor.close();

        ImageView poster = (ImageView) rootView.findViewById(R.id.poster);

        Picasso.with(context)
                .load(baseurl.concat(movie.getMovie_poster_path()))
                .into(poster);

        ImageView backDrop = (ImageView) rootView.findViewById(R.id.backdrop);

        Picasso.with(context)
                .load(baseurl.concat(movie.getMovie_backdrop_path()))
                .into(backDrop);

        TextView titleBar = (TextView) rootView.findViewById(R.id.title_bar);

        titleBar.setText(movie.getMovie_title());

        TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
        releaseDate.setText(movie.getMovie_release_date());

        TextView summaryText = (TextView) rootView.findViewById(R.id.summary_text);
        summaryText.setText(movie.getMovie_overview());

        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        float rating = Float.parseFloat(String.valueOf(movie.getMovie_vote_average()));
        ratingBar.setRating((float) (rating/2.0));

        return rootView;
    }

    public void setPassedMovie(long passedMovie) {
        this.passedMovie = passedMovie;
    }
}
