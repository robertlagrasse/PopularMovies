package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by robert on 9/28/16.
 */

public class DatabaseAdapter {

    // Reference all of the fields in the contract in order to build DB creation queries.

    // Context of the application using this adapter
    private final Context context;

    private DBManager DatabaseManager;
    private SQLiteDatabase database;

    // Public Methods


    // Constructor
    public DatabaseAdapter(Context context) {
        this.context = context;
        DatabaseManager = new DBManager(context);
    }

    // Open a connection to the database
    public DatabaseAdapter open() {
        database = DatabaseManager.getWritableDatabase();
        return this;
    }

    // Close the connection to the database
    public void close() {
        DatabaseManager.close();
    }

    public void bulkInsert(ArrayList<MovieObject> Movies){
        ContentValues values = new ContentValues();

        // Log.e("bulkInsert", "Iteration started...");
        // Iterate through the array we received
        for (MovieObject movie : Movies){
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
            // values.put(TMDBContract.MovieEntry.MOVIE_RESULT_TYPE, movie.getMovie_result_type());
            long dbkey = database.insert(TMDBContract.MovieEntry.TABLE_NAME, null, values);
            // Log.e("bulkInsert", movie.getMovie_title() + " inserted at: " + dbkey);
        }
    }

    public long itemInsert(MovieObject movie){
        ContentValues values = new ContentValues();

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
        // values.put(TMDBContract.MovieEntry.MOVIE_RESULT_TYPE, movie.getMovie_result_type());
        return database.insert(TMDBContract.MovieEntry.TABLE_NAME, null, values);
    }

    public int getDBCount(){
        String dbString = "";

        // Build a sql query
        String query = "SELECT COUNT(*) FROM " + TMDBContract.MovieEntry.TABLE_NAME + " WHERE 1";

        // Point the cursor at the result of the executed query
        Cursor c= database.rawQuery(query, null);
        c.moveToFirst();
        int count = c.getInt(0);
        c.close();
        return count;
    }

    public Cursor getAllRows() {
        String where = null;
        Cursor c = 	database.query(true, TMDBContract.MovieEntry.TABLE_NAME, TMDBContract.MovieEntry.MOVIE_ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getData(String id, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(TMDBContract.MovieEntry.TABLE_NAME);

        if(id != null) {
            sqliteQueryBuilder.appendWhere(TMDBContract.MovieEntry.MOVIE_ID + " = " + id);
        }

        if(sortOrder == null || sortOrder == "") {
            sortOrder = TMDBContract.MovieEntry.MOVIE_ID;
        }
        Cursor cursor = sqliteQueryBuilder.query(database,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return cursor;
    }

    // Inner class
    private class DBManager extends SQLiteOpenHelper {
        // Define some global database stuff
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "database";
        private final Uri DATABASE_URI =
                Uri.parse("content://com.example.android.popularMovies/movies");


        DBManager(Context context) {
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

    }
}
