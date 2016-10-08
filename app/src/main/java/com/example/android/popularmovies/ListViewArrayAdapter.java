package com.example.android.popularmovies;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.ViewHolder.poster;

/**
 * Created by robert on 10/7/16.
 */

public class ListViewArrayAdapter extends ArrayAdapter<DisplayExtras> {
    public ListViewArrayAdapter(Context context, int resource, List<DisplayExtras> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DisplayExtras extras = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.display_listview_item,parent,false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.listview_item_image);
        int pickview = R.drawable.icon128;

        if (extras.getType().equals("reviews")){
            pickview = R.drawable.review;
        }
        Picasso.with(getContext())
                .load(pickview)
                .into(image);
        return convertView;
    }
}
