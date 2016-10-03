package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import java.net.URI;

import static android.R.attr.name;
import static android.icu.text.DateTimePatternGenerator.PatternInfo.CONFLICT;
import static android.webkit.WebSettings.PluginState.ON;


/**
 * Created by robert on 9/26/16.
 * This class contains database, table, and column constants
 * related to TMDB objects as well as user favorites.
 */
public class TMDBContract {

    // Define some SQL Data Types - makes table creation code cleaner and less error prone
    private static final String     VARCHAR_255         = " VARCHAR(255), ";
    private static final String     INTEGER             = " INTEGER, ";
    private static final String     FLOAT               = " FLOAT, ";
    // private static final String     BOOLEAN             = " BOOLEAN, ";

    // This defines the content authority
    public static final String CONTENT_AUTHORITY    = "com.example.android.popularmovies";

    // This is the base path for our URI
    public static final Uri BASE_CONTENT_URI        = Uri.parse("content://" + CONTENT_AUTHORITY);

    // This path matches the movies table
    public static final String MOVIE_PATH = "movies";



    public static final class MovieEntry implements BaseColumns {

        // The content URI represents the base location for this table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_PATH).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;

        public static Uri buildMovieURI(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        /*
        This class defines the properties of each movie. This is identical to
        the fields as they come down in JSON from TMDB.

        The exception is result type. This additional field will keep
        track of whether the movie's presence in the database is
        the result of a search based on popularity, or rating
     */

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
        public static final String MOVIE_TOP_RATED            = "top_rated";
        public static final String MOVIE_MOST_POPULAR         = "most_popular";
        public static final String MOVIE_USER_FAVORITE        = "user_favorite";


        public static final String[] MOVIE_ALL_KEYS = new String[] {
                COLUMN_UID,
                MOVIE_POSTER_PATH,
                MOVIE_ADULT,
                MOVIE_OVERVIEW,
                MOVIE_RELEASE_DATE,
                MOVIE_GENRE_IDS,
                MOVIE_ID,
                MOVIE_ORIGINAL_TITLE,
                MOVIE_ORIGINAL_LANGUAGE,
                MOVIE_TITLE,
                MOVIE_BACKDROP_PATH,
                MOVIE_POPULARITY,
                MOVIE_VOTE_COUNT,
                MOVIE_VIDEO,
                MOVIE_VOTE_AVERAGE,
                MOVIE_TOP_RATED,
                MOVIE_MOST_POPULAR,
                MOVIE_USER_FAVORITE};

        public static final String CREATE_TABLE               =
                        "CREATE TABLE "            +
                        TABLE_NAME                 + "(" +
                        COLUMN_UID                 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MOVIE_POSTER_PATH          + VARCHAR_255 +
                        MOVIE_ADULT                + VARCHAR_255 +
                        MOVIE_OVERVIEW             + VARCHAR_255 +
                        MOVIE_RELEASE_DATE         + VARCHAR_255 +
                        MOVIE_GENRE_IDS            + VARCHAR_255 +
                        MOVIE_ID                   + INTEGER +
                        MOVIE_ORIGINAL_TITLE       + VARCHAR_255 +
                        MOVIE_ORIGINAL_LANGUAGE    + VARCHAR_255 +
                        MOVIE_TITLE                + VARCHAR_255 +
                        MOVIE_BACKDROP_PATH        + VARCHAR_255 +
                        MOVIE_POPULARITY           + FLOAT +
                        MOVIE_VOTE_COUNT           + INTEGER +
                        MOVIE_VIDEO                + VARCHAR_255 +
                        MOVIE_VOTE_AVERAGE         + FLOAT +
                        MOVIE_TOP_RATED            + VARCHAR_255 +
                        MOVIE_MOST_POPULAR         + VARCHAR_255 +
                        MOVIE_USER_FAVORITE        + VARCHAR_255 +
                        "UNIQUE ("+ MOVIE_ID +") ON CONFLICT IGNORE);";
    }

    /* Inner class that defines the contents of the UserMetrics table */
    public static final class UserMetrics implements BaseColumns {

        public static final String TABLE_NAME                 = "user_metrics";

        public static final String COLUMN_UID                 = "_id";
        public static final String COLUMN_MOVIE_ID            = "tmdb_id"; // This column ties to one in MovieEntry
        public static final String COLUMN_USER_RATING         = "user_rating";

    }
}

