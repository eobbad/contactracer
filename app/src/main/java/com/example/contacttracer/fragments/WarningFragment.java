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


import com.example.contacttracer.R;
import com.example.contacttracer.models.Warning;
import com.example.contacttracer.WarningsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class WarningFragment extends Fragment {

    public static final String TAG = "WarningFragment";
    private RecyclerView rvWarnings;
    protected WarningsAdapter adapter;
    protected List<Warning> allWarnings;
    public WarningFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_posts, container, false);
        View view = inflater.inflate(R.layout.fragment_warning, container, false);
        // Lookup the swipe container view
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        super.onViewCreated(view, savedInstanceState);
        rvWarnings = view.findViewById(R.id.rvWarnings);
        allWarnings = new ArrayList<>();
        adapter = new WarningsAdapter(getContext(), allWarnings);
        rvWarnings.setAdapter(adapter);
        rvWarnings.setLayoutManager(new LinearLayoutManager(getContext()));
        queryWarnings();
    }

    protected void queryWarnings() {

        ParseQuery<Warning> query = ParseQuery.getQuery(Warning.class);

        query.include(Warning.KEY_USER);
        query.include(Warning.KEY_OTHERUSER);
        query.include(Warning.KEY_STATUS);
        query.whereEqualTo(Warning.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Warning.KEY_STATUS, "Positive");
        query.setLimit(20);
        query.addDescendingOrder(Warning.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Warning>() {
            @Override
            public void done(List<Warning> warnings, ParseException e) {
                if(e != null){
                    Log.e(TAG, "issue with getting warnings", e);
                    return;
                }
                for (Warning warning : warnings){
                    Log.i(TAG, "Warning: " + ", username: ");
                }
                allWarnings.addAll(warnings);
                adapter.notifyDataSetChanged();
            }
        });

    }

}