package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by robert on 10/7/16.
 *
 * Sets the appropriate icon on the listview - trailer or review
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
        int pickview = R.drawable.trailer;

        if (extras.getType().equals("reviews")){
            pickview = R.drawable.review;
        }
        Picasso.with(getContext())
                .load(pickview)
                .into(image);
        return convertView;
    }
}
