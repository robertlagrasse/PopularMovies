package com.example.android.popularmovies;

import java.util.ArrayList;

/**
 * Created by robert on 9/10/16.
 *
 * This interface provides a mechanism for GridFragment to convey information back to MainActivity.
 *
 * MainActivity implements the interface and the respond() method.
 *
 * GridFragment creates an instance of communicator and points it to getActivity(),
 * thereby creating a reference to MainActivity. communicator.respond() in GridFragment
 * is how the call is made and the ArrayList is passed back to MainActivity.
 *
 */
public interface Communicator {
    void respond();
}
