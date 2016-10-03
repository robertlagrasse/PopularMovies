package com.example.android.popularmovies;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.Manifest.permission_group.LOCATION;
import static android.R.attr.value;
import static com.google.android.gms.security.ProviderInstaller.PROVIDER_NAME;

/**
 * Created by robert on 9/30/16.
 *
 * ContentProviders suck, right up until you begin to see why they don't suck.
 * This content provider essentially front ends DatabaseAdapter.
 *
 * All requests to talk to the database come in a standard URI form. This
 * content provider then parses the URI, and sends the DatabaseAdapter
 * whatever it's expecting to see in order to illicit the appropriate response.
 *
 * This is essentially putting a web server in front of your database resources, and
 * having the web server direct the request to the appropriate places.
 */

public class MovieContentProvider extends ContentProvider {

    DatabaseManager databaseManager;

    // These definitions will move to TMDBContract.
    // Here now for easier reference.
    public static final String CONTENT_AUTHORITY    = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI        = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String MOVIE_PATH           = "movies";

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_PATH).build();

    private static final int ALL_MOVIES     = 0;
    private static final int ONE_MOVIE      = 1;
    private static final int FAVORITES      = 2;
    private static final int POPULAR        = 3;
    private static final int TOP_RATED      = 4;

    private static final UriMatcher uriMatcher = getUriMatcher();

    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TMDBContract.CONTENT_AUTHORITY, "movies", ALL_MOVIES);
        uriMatcher.addURI(TMDBContract.CONTENT_AUTHORITY, "movies/#", ONE_MOVIE);
        uriMatcher.addURI(TMDBContract.CONTENT_AUTHORITY, "movies/favorites", FAVORITES);
        uriMatcher.addURI(TMDBContract.CONTENT_AUTHORITY, "movies/popular", POPULAR);
        uriMatcher.addURI(TMDBContract.CONTENT_AUTHORITY, "movies/top_rated", TOP_RATED);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        databaseManager = new DatabaseManager(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        /*
        Cursor query (Uri uri,
                String[] projection,
                String selection,
                String[] selectionArgs,
                String sortOrder)
        Query the given URI, returning a Cursor over the result set.

        For best performance, the caller should follow these guidelines:

        Provide an explicit projection, to prevent reading
        data from storage that aren't going to be used.

        Use question mark parameter markers such as 'phone=?' instead of explicit values in the
        selection parameter, so that queries that differ only by those values will be recognized
        as the same for caching purposes.

        Parameters
        ----------
        uri	Uri: The URI, using the content:// scheme, for the content to retrieve.
        projection	String: A list of which columns to return. Passing null will return all columns, which is inefficient.
        selection	String: A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given URI.
        selectionArgs	String: You may include ?s in selection, which will be replaced by the values from selectionArgs, in the order that they appear in the selection. The values will be bound as Strings.
        sortOrder	String: How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.

         */

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (getUriMatcher().match(uri)) {

            // The GridFragment will ask for multiple movies
            // It needs to see poster path
            // Later, it will also need to pull favorites status. This will require some
            // database modifications.

            case ALL_MOVIES: {
                Log.e("MovieContentProvider", "projection: " + projection);
                retCursor = databaseManager.getReadableDatabase().query(
                        TMDBContract.MovieEntry.TABLE_NAME,
                        new String[]{TMDBContract.MovieEntry.MOVIE_POSTER_PATH},    // poster path
                        null,                                                       // no filter
                        null,                                                       // no args
                        null,
                        null,
                        null                                                        // no sorting
                );
                break;
            }

            // One Movie would be requested by the DisplayFragment, currently a stand alone
            // We'll need title, release date, poster path, background path, rating
            // and description. This will probably end up a string array in the db contract.
            // for now, I just want it off the ground.

            case ONE_MOVIE: {
                Log.e("MovieContentProvider", "uri " + uri + " Matched ONE_MOVIE");
                String movie_id = uri.getPathSegments().get(1);
                retCursor = databaseManager.getReadableDatabase().query(
                        TMDBContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            }
            case FAVORITES: {
                Log.e("MovieContentProvider", "uri " + uri + " Matched FAVORITES");

                retCursor = databaseManager.getReadableDatabase().query(
                        TMDBContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case POPULAR: {
                Log.e("MovieContentProvider", "uri " + uri + " Matched POPULAR");

                retCursor = databaseManager.getReadableDatabase().query(
                        TMDBContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TOP_RATED: {
                Log.e("MovieContentProvider", "uri " + uri + " Matched TOP_RATED");

                retCursor = databaseManager.getReadableDatabase().query(
                        TMDBContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // Some quick definitions to make the switch statement cleaner.
        final String DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;
        final String ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;

        switch (uriMatcher.match(uri)) {
            case ALL_MOVIES:
                return DIR;
            case ONE_MOVIE:
                return ITEM;
            case FAVORITES:
                return DIR;
            case POPULAR:
                return DIR;
            case TOP_RATED:
                return DIR;
        }
        return "";
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = databaseManager.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        returnUri = uri;
        switch (match) {
            case ALL_MOVIES: {
                // Log.e("CP_insert", contentValues.getAsString(TMDBContract.MovieEntry.MOVIE_TITLE));
                long _id = db.insert(TMDBContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (_id < 0) {
                    // Log.e("CP_insert", "Did not insert :" + contentValues.getAsString(TMDBContract.MovieEntry.MOVIE_TITLE));
                    returnUri = Uri.withAppendedPath(uri, String.valueOf(_id));
                }
                break;
            }
            // More cases will follow, but for other tables.
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = databaseManager.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case ONE_MOVIE:
                rowsDeleted = db.delete(
                        TMDBContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            // additional cases as necessary
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = databaseManager.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ONE_MOVIE:
                String movie = uri.getPathSegments().get(1);
                rowsUpdated = db.update(TMDBContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
//        if (rowsUpdated != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
//        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = databaseManager.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case ALL_MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TMDBContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
