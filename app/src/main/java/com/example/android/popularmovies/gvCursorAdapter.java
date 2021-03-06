package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by robert on 10/3/16.
 */

public class gvCursorAdapter extends CursorAdapter {
    public gvCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // This is the poster image
        ImageView poster = (ImageView) view.findViewById(R.id.poster_image);

        // This is the icon I use to designate a favorite
        ImageView favorite = (ImageView) view.findViewById(R.id.favorite_marker);

        // This grabs the relevant data from the database
        String path = cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_POSTER_PATH));
        String isFavorite = cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_USER_FAVORITE));

        // This is where we find images.
        String baseurl = "http://image.tmdb.org/t/p/w185";

        // Check to see if we should show the favorites icon
        if (isFavorite.equals("true")){
            favorite.setVisibility(View.VISIBLE);
        }
        else {
            favorite.setVisibility(View.INVISIBLE);
        }
        // Set the scaling
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Load the poster image
        Picasso.with(context).load(baseurl.concat(path)).placeholder(R.drawable.blank).into(poster);


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }
}