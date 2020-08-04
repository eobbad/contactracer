package com.example.contacttracer.models;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;
public class ContactInfo{
    public static long time;
    public static LatLng location;
    public ContactInfo(long time, LatLng location){
        this.time = time;
        this.location = location;
    }
}
