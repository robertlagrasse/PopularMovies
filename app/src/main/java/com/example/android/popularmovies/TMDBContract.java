package com.example.android.popularmovies;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by robert on 9/26/16.
 * This class contains database, table, and column constants
 * related to TMDB objects as well as user favorites.
 */
public class TMDBContract {

    /*
        This class defines the properties of each movie. This is identical to
        the fields as they come down in JSON from TMDB.

        The exception is result type. This additional field will keep
        track of whether the movie's presence in the database is
        the result of a search based on popularity, or rating
     */
    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME                 = "movies";
        public static final String COLUMN_UID                 = "_id";
        public static final String MOVIE_POSTER_PATH          = "poster_path";
        public static final String MOVIE_ADULT                = "adult";
        public static final String MOVIE_OVERVIEW             = "overview";
        public static final String MOVIE_RELEASE_DATE         = "release_date";
        public static final String MOVIE_GENRE_IDS            = "genre_ids";
        public static final String MOVIE_ID                   = "tmdb_id";
        public static final String MOVIE_ORIGINAL_TITLE       = "original_title";
        public static final String MOVIE_ORIGINAL_LANGUAGE    = "original_language";
        public static final String MOVIE_TITLE                = "title";
        public static final String MOVIE_BACKDROP_PATH        = "backdrop_path";
        public static final String MOVIE_POPULARITY           = "popularity";
        public static final String MOVIE_VOTE_COUNT           = "vote_count";
        public static final String MOVIE_VIDEO                = "video";
        public static final String MOVIE_VOTE_AVERAGE         = "vote_average";
        public static final String MOVIE_RESULT_TYPE          = "result_type";


    }

    /* Inner class that defines the contents of the weather table */
    public static final class UserMetrics implements BaseColumns {

        public static final String TABLE_NAME = "user_metrics";

        public static final String COLUMN_UID                 = "_id";
        public static final String COLUMN_MOVIE_ID            = "tmdb_id"; // This column ties to one in MovieEntry
        public static final String COLUMN_USER_RATING         = "user_rating";

    }
}

