package com.example.contacttracer.Activity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.contacttracer.Activity.LoginActivity;
import com.example.contacttracer.GPSTracker;
import com.example.contacttracer.R;
import com.example.contacttracer.fragments.HistoryFragment;
import com.example.contacttracer.fragments.StatusFragment;
import com.example.contacttracer.fragments.WarningFragment;
import com.example.contacttracer.models.ContactInfo;
import com.example.contacttracer.models.Warning;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveCurrentUserLocation();
        putNearbyUsers();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = new WarningFragment();
                switch (menuItem.getItemId()) {
                    case R.id.action_warning:
                        fragment = new WarningFragment() ;
                        break;
                    case R.id.action_history:
                        fragment = new HistoryFragment();
                        break;
                    case R.id.action_logOut:
                        ParseUser.logOut();
                        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                        goLoginActivity();
                        break;
                    case R.id.action_status:
                        fragment = new StatusFragment();
                        break;
                    default:
                        fragment = new WarningFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_warning);
    }

    private void putNearbyUsers() {
        //add to list of users I came into contact with
        final ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        final ParseGeoPoint currentLocation = currentUser.getParseGeoPoint("Location");
        query.whereNear("Location", currentLocation);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override  public void done(List<ParseUser> nearUsers, ParseException e) {
                if (e == null) {

                    List<Integer> rem = new ArrayList<>();
                    for(int i = nearUsers.size()-1; i > 0; i--) {

                        //lets us know if the user we are iterating through has been deleted
                        Boolean deleted = false;
                        //get users location
                        ParseUser thisUser = nearUsers.get(i);
                        System.out.println(thisUser.getUsername());
                        ParseGeoPoint thisUserLocation = thisUser.getParseGeoPoint("Location");
                        //if this user is the current user remove from arraylist
                        if(thisUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            nearUsers.remove(i);
                            deleted = true;
                            System.out.println(thisUser.getUsername() + " is current");
                        }
                        //or if this user not is within 15 miles(or 24 km) from the current user, remove from list
                        if(currentLocation.distanceInKilometersTo(thisUserLocation)>1000.0){
                            //check if we already deleted this user
                            if(deleted == false){
                                nearUsers.remove(i);
                                deleted = true;
                                System.out.println(thisUser.getUsername() + "failed not near");

                            }
                        }
                        //if this user wasn't deleted then we want to add them to the hashmap
                        if(deleted == false){
                            System.out.println(thisUser.getUsername() + " made it");

                            //create contact info object with date and location
                            // delete previous warning with this user
                            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Warning");
                            query1.whereEqualTo("OtherUser", thisUser);
                            query1.getFirstInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {

                                    Boolean objectNull;
                                    if(object == null){
                                        objectNull = true;
                                    }else{
                                        objectNull = false;
                                    }
                                    try {
                                        if(!objectNull) {
                                            object.delete();
                                        }
                                    } catch (ParseException ex) {
                                        ex.printStackTrace();
                                    }
                                    if(!objectNull) {
                                        object.saveInBackground();
                                    }
                                }
                            });
                            //System.out.println("4");
                            //Then save the new Warning
                            Warning warning = new Warning();
                            warning.setUser(currentUser);
                            warning.setOtherUser(thisUser);
                            warning.setLocation(getAddress(currentLocation.getLatitude(),currentLocation.getLongitude()));
                            warning.setDescription("Close contact with a person infected with COVID-19");
                            warning.setStatus((String) thisUser.get("status"));
                            warning.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if( e != null){
                                        Log.e(TAG, "Error while saving", e);
                                    }
                                    Log.i(TAG, "Warning save was successful!!");
                                }
                            });
                        }

                    }

                    //update the users we came into contact with during this login
                    currentUser.addAll("contacts", nearUsers);
                    currentUser.saveInBackground();

                } else {
                    Log.d("store", "Error: " + e.getMessage());
                }
            }
        });
    }

    private String getAddress(double latitude, double longitude) {

        String result = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (list != null && list.size() > 0) {
                Address address = list.get(0);
                // sending back first address line and locality

                result = address.getAddressLine(0) + ", " + address.getLocality();
            }
        } catch (IOException e) {
            Log.e(TAG, "Impossible to connect to Geocoder", e);
        }

        return result;
    }


    private void goLoginActivity() {

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void saveCurrentUserLocation() {
        // requesting permission to get user's location


            GPSTracker gpsTracker = new GPSTracker(this);
            Double myLat = gpsTracker.getLatitude();
            Double myLong = gpsTracker.getLongitude();
            System.out.println("lat: " + myLat + " long: " + myLong);

            //Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            // checking if the location is null
            if(myLat != null && myLong != null){

                // if it isn't, save it to Back4App Dashboard
                ParseGeoPoint currentUserLocation = new ParseGeoPoint(myLat,myLong);
                ParseUser currentUser = ParseUser.getCurrentUser();

                if (currentUser != null) {
                    currentUser.put("Location", currentUserLocation);
                    currentUser.saveInBackground();
                } else {
                    // do something like coming back to the login activity
                }
            }
            else {
                // if it is null, do something like displaying error and coming back to the menu activity
            }
        }


    //gets parse Geopoint from the current user
    private ParseGeoPoint getCurrentUserLocation(){

        // finding currentUser
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            // if it's not possible to find the user, do something like returning to login activity
        }
        // otherwise, return the current user location
        return currentUser.getParseGeoPoint("Location");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_LOCATION:
                saveCurrentUserLocation();
                break;
        }
    }


}