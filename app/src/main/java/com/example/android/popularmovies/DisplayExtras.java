package com.example.android.popularmovies;

/**
 * Created by robert on 10/7/16.
 *
 * DisplayExtras is a class that keeps track of the reviews and trailers for each movie.
 * Type defines whether it's a trailer or review, location is the URL.
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
