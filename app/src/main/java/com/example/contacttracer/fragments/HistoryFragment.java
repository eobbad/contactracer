package com.example.contacttracer.fragments;

import android.location.Location;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.contacttracer.models.Warning;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class HistoryFragment extends Fragment implements OnMapReadyCallback {

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
}
