package com.example.contacttracer.fragments;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contacttracer.GPSTracker;
import com.example.contacttracer.R;
import com.example.contacttracer.models.Warning;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatusFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "StatusFragment";
    private Spinner sChangeStatus;
    private ImageView ivProfile;
    private TextView tvUsername;
    private TextView tvLocation;

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_posts, container, false)
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        // Lookup the swipe container view
        return view;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        super.onViewCreated(view, savedInstanceState);
        //for now I will only have "positive" and "negative" options
        sChangeStatus = view.findViewById(R.id.sChangeStatus);
        ivProfile = view.findViewById(R.id.ivProfile);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvLocation = view.findViewById(R.id.tvLocation);

        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        GPSTracker gpsTracker = new GPSTracker(getContext());
        Double myLat = gpsTracker.getLatitude();
        Double myLong = gpsTracker.getLongitude();
        try {
            tvLocation.setText(getCityName(myLat,myLong));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //creating the drop down menu
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sChangeStatus.setAdapter(adapter);
        sChangeStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                String text = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();

                ParseUser currentUser = ParseUser.getCurrentUser();
                setStatus(text, currentUser);
            }
            private void setStatus(String text, ParseUser currentUser) {
                //create a warning for this user if they test positive
                Warning warning = new Warning();
                //for now this is hard-coded, might add length of interaction as stretch feature
                warning.setDescription("Close contact with a person infected with COVID-19");
                //hardcoded, will get location from maps sdk
                warning.setLocation("Manhattan Beach, CA");
                warning.setUser(currentUser);
               // warning.setImage(null);
                warning.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if( e != null){
                            Log.e(TAG, "Error while saving", e);
                            Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                        }
                        Log.i(TAG, "Warning save was successful!!");
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public String getCityName(Double latitude, Double longitude) throws IOException {

        String result = null;
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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
}