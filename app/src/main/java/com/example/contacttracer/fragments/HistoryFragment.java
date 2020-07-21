package com.example.contacttracer.fragments;

import android.util.Log;

import com.example.contacttracer.models.Warning;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class HistoryFragment extends WarningFragment{

    @Override
    protected void queryWarnings() {
        ParseQuery<Warning> query = ParseQuery.getQuery(Warning.class);
        query.include(Warning.KEY_USER);
        query.whereEqualTo(Warning.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Warning.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Warning>() {
            @Override
            public void done(List<Warning> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "issue with getting posts", e);
                    return;
                }
                for (Warning post : posts){
                    Log.i(TAG, "Post: " + post.getDescription()+ ", username: "+ post.getUser().getUsername());
                }
                allWarnings.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
