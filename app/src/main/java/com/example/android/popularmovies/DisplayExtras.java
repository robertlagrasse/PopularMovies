package com.example.android.popularmovies;

import android.net.Uri;

import static android.R.attr.type;

/**
 * Created by robert on 10/7/16.
 */

public class DisplayExtras {
    String type;
    String location;

    public DisplayExtras() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
