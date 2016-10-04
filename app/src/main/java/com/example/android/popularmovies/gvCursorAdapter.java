package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ResourceCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.R.attr.path;
import static java.security.AccessController.getContext;

/**
 * Created by robert on 10/3/16.
 */

public class gvCursorAdapter extends CursorAdapter {
    public gvCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view;
        String path = cursor.getString(cursor.getColumnIndex(TMDBContract.MovieEntry.MOVIE_POSTER_PATH));
        String baseurl = "http://image.tmdb.org/t/p/w185";
        Picasso.with(context).load(baseurl.concat(path)).into(imageView);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView iView = new ImageView(context);
        iView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iView.setAdjustViewBounds(true);
        return iView;
    }
}