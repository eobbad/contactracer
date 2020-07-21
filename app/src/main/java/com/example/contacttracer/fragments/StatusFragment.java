package com.example.contacttracer.fragments;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.contacttracer.R;
import com.example.contacttracer.models.Warning;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class StatusFragment extends Fragment {

    public static final String TAG = "StatusFragment";
    private EditText etDescription;
    private Button BtnStatus;

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

        etDescription = view.findViewById(R.id.etDescription);
        BtnStatus = view.findViewById(R.id.BtnStatus);

        BtnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etDescription.getText().toString();

                if(description.isEmpty()){
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                ParseUser currentUser = ParseUser.getCurrentUser();

            }
        });


    }


}