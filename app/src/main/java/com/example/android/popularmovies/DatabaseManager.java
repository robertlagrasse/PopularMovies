package com.example.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by robert on 9/30/16.
 */

public class DatabaseManager extends SQLiteOpenHelper {
    // Define some global database stuff
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database";
    private final Uri DATABASE_URI =
            Uri.parse("content://com.example.android.popularMovies/movies");


    DatabaseManager (Context context) {
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