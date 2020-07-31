package com.example.contacttracer.fragments;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

import android.os.Environment;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.example.contacttracer.GPSTracker;
import com.example.contacttracer.R;
import com.example.contacttracer.models.Warning;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class StatusFragment extends Fragment {

    ParseUser currentUser = ParseUser.getCurrentUser();
    public static final String TAG = "StatusFragment";
    private Spinner sChangeStatus;
    private ImageView ivProfile;
    private TextView tvUsername;
    private TextView tvLocation;
    private Button btnChangeProfile;

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int REQUEST_CODE = 43;


    private File photoFile;
    public String photoFileName = "photo.jpg";

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
        btnChangeProfile = view.findViewById(R.id.BtnChangeProfile);

        String userStatus = currentUser.getString("status");

        loadImage();

        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchCamera();
                if (photoFile == null || ivProfile.getDrawable() == null){
                    Toast.makeText(getContext(), "There is no image", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveImage(photoFile);

            }
        });
        //creating the drop down menu
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sChangeStatus.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(userStatus);
        sChangeStatus.setSelection(spinnerPosition);


        sChangeStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                String text = parent.getItemAtPosition(position).toString();

                ParseUser currentUser = ParseUser.getCurrentUser();
                setStatus(text, currentUser);
                setCurrentUserStatus(text, currentUser);
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

        tvUsername.setText(ParseUser.getCurrentUser().getUsername());

        GPSTracker gpsTracker = new GPSTracker(getContext());
        Double myLat = gpsTracker.getLatitude();
        Double myLong = gpsTracker.getLongitude();

        try {
            System.out.println(getCityName(myLat, myLong));
            tvLocation.setText(getCityName(myLat,myLong));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setCurrentUserStatus(String text, ParseUser currentUser) {

        if (currentUser != null) {
            currentUser.put("status", text);
            currentUser.saveInBackground();
        } else {
            // do something like coming back to the login activity
        }
    }

    private void loadImage() {

        ParseFile image = currentUser.getParseFile("Image");

        if(image != null){
            Glide.with(getContext()).load(image.getUrl()).into(ivProfile);
        }
    }

    public void saveImage(File photoFile){

        ParseFile profilePic = new ParseFile(photoFile);
        if (currentUser != null) {
            currentUser.put("Image", profilePic);
            currentUser.saveInBackground();
        } else {
            // do something like coming back to the login activity
        }
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.contactracer", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivProfile.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
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