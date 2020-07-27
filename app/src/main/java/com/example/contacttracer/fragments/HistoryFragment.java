package com.example.contacttracer.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.contacttracer.GPSTracker;
import com.example.contacttracer.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class HistoryFragment extends Fragment{

    //I have to use a mapView instead of a
    private MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_posts, container, false)

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                // For showing a move to my location button
                //permission check
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    System.out.println("map not accessbile");
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                // For dropping a marker at a point on the Map
                //get my location(which is updated every minute
                GPSTracker gpsTracker = new GPSTracker(getContext());
                Double myLat = gpsTracker.getLatitude();
                Double myLong = gpsTracker.getLongitude();

                System.out.println("Lat: "+myLat + "Long" + myLong);
                LatLng myLocation = new LatLng(myLat, myLong);

                ParseUser currentUser = ParseUser.getCurrentUser();
                ParseQuery<ParseUser> query = ParseUser.getQuery();

                final ParseGeoPoint currentLocation = currentUser.getParseGeoPoint("Location");
                query.whereNear("Location", currentLocation);

                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override  public void done(List<ParseUser> nearUsers, ParseException e) {
                        if (e == null) {
                            // avoiding null pointer
                            ParseUser closestUser = ParseUser.getCurrentUser();
                            // set the closestUser to the one that isn't the current user
                            for(int i = 0; i < nearUsers.size(); i++) {
                                if(!nearUsers.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                    closestUser = nearUsers.get(i);
                                }
                            }
                            // finding and displaying the distance between the current user and the closest user to him
                            double distance = currentLocation.distanceInKilometersTo(closestUser.getParseGeoPoint("Location"));
                            // creating a marker in the map showing the closest user to the current user location
                            LatLng closestUserLocation = new LatLng(closestUser.getParseGeoPoint("Location").getLatitude(), closestUser.getParseGeoPoint("Location").getLongitude());

                            googleMap.addMarker(new MarkerOptions().position(closestUserLocation).icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        } else {
                            Log.d("store", "Error: " + e.getMessage());
                        }
                    }
                });

                //googleMap.addMarker(new MarkerOptions().position(myLocation).title("Marker Title").snippet("Marker Description"));
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        // Lookup the swipe container view
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}


