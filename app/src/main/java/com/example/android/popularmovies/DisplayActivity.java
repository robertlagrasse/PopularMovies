package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
/* DisplayActivity exists to display detail information about a specific movie.
 * It's launched by intent, and the intent includes a ArrayList.
 *
 * The ArrayList is picked apart, and the elements are used to populate the
 * views in the layout.
 */

public class DisplayActivity extends AppCompatActivity {
    // The arraylist of extras passed with the intent is called "movie_details"
    public final static String MOVIE_DETAILS = "movie_details";

    // This is the base path for all images.
    final String baseurl = "http://image.tmdb.org/t/p/w780";

    // Human readable positions in the ArrayList
    final int MOVIE_POSTER_PATH          = 0;
    final int MOVIE_ADULT                = 1;
    final int MOVIE_OVERVIEW             = 2;
    final int MOVIE_RELEASE_DATE         = 3;
    final int MOVIE_GENRE_IDS            = 4;
    final int MOVIE_ID                   = 5;
    final int MOVIE_ORIGINAL_TITLE       = 6;
    final int MOVIE_ORIGINAL_LANGUAGE    = 7;
    final int MOVIE_TITLE                = 8;
    final int MOVIE_BACKDROP_PATH        = 9;
    final int MOVIE_POPULARITY           = 10;
    final int MOVIE_VOTE_COUNT           = 11;
    final int MOVIE_VIDEO                = 12;
    final int MOVIE_VOTE_AVERAGE         = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the view
        setContentView(R.layout.display_activity);

        // Get reference to current intent
        Intent intent = getIntent();

        // Grab the Arraylist passed in with this intent
        ArrayList<String> movie = intent.getStringArrayListExtra(MOVIE_DETAILS);

        // Grab a reference to the poster
        ImageView poster = (ImageView) findViewById(R.id.poster);

        // Use Picasso to display the image
        Picasso.with(this)
                .load(baseurl.concat(movie.get(MOVIE_POSTER_PATH)))
                .into(poster);

        // Grab a reference to the backdrop
        ImageView backDrop = (ImageView) findViewById(R.id.backdrop);

        // Use Picasso to display the image
        Picasso.with(this)
                .load(baseurl.concat(movie.get(MOVIE_BACKDROP_PATH)))
                .into(backDrop);


        // Grab a reference to the titleBar
        TextView titleBar = (TextView) findViewById(R.id.title_bar);
        // Set the text
        titleBar.setText(movie.get(MOVIE_TITLE));

        // Grab a reference to the ReleaseDate
        TextView releaseDate = (TextView) findViewById(R.id.release_date);
        // Set the text
        releaseDate.setText(movie.get(MOVIE_RELEASE_DATE));

        // Grab a reference to the summaryText
        TextView summaryText = (TextView) findViewById(R.id.summary_text);
        // Set the text
        summaryText.setText(movie.get(MOVIE_OVERVIEW));

        //Grab a reference to the ratingBar
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        float rating = Float.parseFloat(movie.get(MOVIE_VOTE_AVERAGE));
        // TMDB is on a 10 point system, we show on a 5 star scale
        ratingBar.setRating((float) (rating/2.0));


    }
}
