package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.example.android.popularmovies.TMDBContract;

import java.net.URI;

/**
 * Created by robert on 9/27/16.
 */
public class DBManager extends SQLiteOpenHelper {
    // Define some global database stuff
    private static final int        DATABASE_VERSION    = 1;
    private static final String     DATABASE_NAME       = "database";
    private static final Uri DATABASE_URI        =
            Uri.parse("content://com.example.android.popularMovies/movies");



    // Cleaned up this constructor. The only thing that got
    // used in the original call was context, so here we are.
    public DBManager(Context context) {
        //super(context, name, factory, version); // original super.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TMDBContract.MovieEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // delete the existing database
        db.execSQL("DROP TABLE IF EXISTS " + TMDBContract.MovieEntry.TABLE_NAME);
        // call onCreate
        onCreate(db);
    }

    public void addMovie(MovieObject movie){
        // get reference to the database
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        // Associate column name with value
        values.put(TMDBContract.MovieEntry.MOVIE_POSTER_PATH, movie.getMovie_poster_path());
        values.put(TMDBContract.MovieEntry.MOVIE_ADULT	, movie.getMovie_adult());
        values.put(TMDBContract.MovieEntry.MOVIE_OVERVIEW, movie.getMovie_overview());
        values.put(TMDBContract.MovieEntry.MOVIE_RELEASE_DATE,	movie.getMovie_release_date());
        values.put(TMDBContract.MovieEntry.MOVIE_GENRE_IDS, movie.getMovie_genre_ids());
        values.put(TMDBContract.MovieEntry.MOVIE_ID, movie.getMovie_id());
        values.put(TMDBContract.MovieEntry.MOVIE_ORIGINAL_TITLE, movie.getMovie_original_title());
        values.put(TMDBContract.MovieEntry.MOVIE_ORIGINAL_LANGUAGE, movie.getMovie_original_language());
        values.put(TMDBContract.MovieEntry.MOVIE_TITLE, movie.getMovie_title());
        values.put(TMDBContract.MovieEntry.MOVIE_BACKDROP_PATH, movie.getMovie_backdrop_path());
        values.put(TMDBContract.MovieEntry.MOVIE_POPULARITY, movie.getMovie_popularity());
        values.put(TMDBContract.MovieEntry.MOVIE_VOTE_COUNT, movie.getMovie_vote_count());
        values.put(TMDBContract.MovieEntry.MOVIE_VIDEO, movie.getMovie_video());
        values.put(TMDBContract.MovieEntry.MOVIE_VOTE_AVERAGE, movie.getMovie_vote_average());
        values.put(TMDBContract.MovieEntry.MOVIE_RESULT_TYPE, movie.getMovie_result_type());


        // insert value into table at the appropriate location
        db.insert(TMDBContract.MovieEntry.TABLE_NAME, null, values);
        // done writing
        db.close();
    }

    // Delete an item from the database
    public void delMovie(int movieID){
        // Get database reference
        SQLiteDatabase db = getWritableDatabase();

        // build the sql query
        // ********* This query may be broken. See how deleting with an integer
        // as the sql search term works. May need to remove the \"'s
        String query =  "DELETE FROM "  + TMDBContract.MovieEntry.TABLE_NAME +
                " WHERE "       + TMDBContract.MovieEntry.MOVIE_ID +
                "=\""           + movieID + "\";";
        //execute the query
        db.execSQL(query);
    }

    public String dbToString(){
        // Get a reference to the database
        SQLiteDatabase db = getWritableDatabase();
        String dbString = "";

        // Build a sql query
        String query = "SELECT * FROM " + TMDBContract.MovieEntry.TABLE_NAME + " WHERE 1";

        // Point the cursor at the result of the executed query
        Cursor c = db.rawQuery(query, null);

        // Start at the beginning
        c.moveToFirst();

        // Iterate through the rows until the end is reached
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(TMDBContract.MovieEntry.COLUMN_UID))!=null){
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.COLUMN_UID));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_POSTER_PATH));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_ADULT));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_OVERVIEW));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_RELEASE_DATE));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_GENRE_IDS));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_ID));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_ORIGINAL_TITLE));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_ORIGINAL_LANGUAGE));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_TITLE));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_BACKDROP_PATH));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_POPULARITY));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_VOTE_COUNT));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_VIDEO));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_VOTE_AVERAGE));
                dbString += "\n";
                dbString += c.getString(c.getColumnIndex(TMDBContract.MovieEntry.MOVIE_RESULT_TYPE));
                dbString += "\n";
            }
            // point to the next row
            c.moveToNext();
        }
        // close the database
        db.close();
        // send back the data
        return dbString;
    }
}
