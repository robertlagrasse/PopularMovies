package com.example.android.popularmovies;

import android.view.View;
import android.widget.ImageView;

/**
 * Created by robert on 10/5/16.
 */

public class ViewHolder {
    public static ImageView poster;
    public static ImageView favorite;

    public ViewHolder(View view) {
        poster = (ImageView) view.findViewById(R.id.poster_image);
        favorite = (ImageView) view.findViewById(R.id.favorite_marker);
        poster.setAdjustViewBounds(true);
    }
}
