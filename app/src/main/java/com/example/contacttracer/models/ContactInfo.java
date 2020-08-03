package com.example.contacttracer.models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;

public class ContactInfo {

    private long time;
    private LatLng locaton;
    private ParseUser user;

    public ContactInfo(long time, ParseUser user, LatLng location){

    }

}
