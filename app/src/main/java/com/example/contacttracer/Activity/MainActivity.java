package com.example.contacttracer.Activity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.contacttracer.Activity.LoginActivity;
import com.example.contacttracer.GPSTracker;
import com.example.contacttracer.R;
import com.example.contacttracer.fragments.HistoryFragment;
import com.example.contacttracer.fragments.StatusFragment;
import com.example.contacttracer.fragments.WarningFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private static final int REQUEST_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveCurrentUserLocation();

        //Parse method that shows users close to you(within 100 miles I believe)
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNear("Location", getCurrentUserLocation());
        //nearUsers should display the users that are close to the current user
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override  public void done(List<ParseUser> nearUsers, ParseException e) {
                if (e == null) {
                    // do something with the list of results of your query
                } else {
                    // handle the error
                }
            }
        });
        ParseQuery.clearAllCachedResults();
        
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


    private void goLoginActivity() {

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void saveCurrentUserLocation() {
        // requesting permission to get user's location
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {
            // getting last know user's location

            GPSTracker gpsTracker = new GPSTracker(this);
            Double myLat = gpsTracker.getLatitude();
            Double myLong = gpsTracker.getLongitude();
            // checking if the location is null
            if(myLat != null && myLong != null){
                // if it isn't, save it to Back4App Dashboard
                ParseGeoPoint currentUserLocation = new ParseGeoPoint(myLat, myLong);

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