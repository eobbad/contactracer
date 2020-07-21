package com.example.contacttracer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.contacttracer.Activity.LoginActivity;
import com.example.contacttracer.R;
import com.example.contacttracer.fragments.HistoryFragment;
import com.example.contacttracer.fragments.StatusFragment;
import com.example.contacttracer.fragments.WarningFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {




    public static final String TAG = "MainActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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


}