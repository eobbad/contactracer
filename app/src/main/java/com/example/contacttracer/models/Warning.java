package com.example.contacttracer.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//each warning will have the name and profile picture of the person who's infected
//it will also have the location of the meeting
@ParseClassName("Warning")
public class Warning extends ParseObject{

    //I will have to do more logic into finding the user that I come into close contact with
    public static final String KEY_USER = "user";
    public static final String KEY_OTHERUSER = "OtherUser";
    //Here I am getting the profile picture for the user that I came into close contact with
    public static final String KEY_IMAGE = "image";
    public static final String KEY_LOCATION = "location";
    //I will have to do some logic later in order to figure out the time that both users came into contact
    public static final String KEY_CREATED_AT = "createdAt";
    //For now I am hardcoding the description, I may change this as a stretch goal
    public static final String KEY_DESCRIPTION = "description";
    //I will have to do some logic in order to calculate the location that both users met
    //public static final String KEY_LOCATION = "";
    //get profile picture

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }

    public String getDescription(){
        return "Close contact with a person infected with COVID-19";
    }
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    //temporary method
    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public ParseUser getOtherUser(){
        return getParseUser(KEY_OTHERUSER);
    }
    public void setOtherUser(ParseUser user){
        put(KEY_OTHERUSER, user);
    }

    //temporary method
    public ParseObject getLocation(){return getParseObject(KEY_LOCATION);}
    public void setLocation(String location){
        put(KEY_LOCATION, location);
    }

    //temporary method
    public String getCreatedTime() {
        Date date = getCreatedAt();
        DateFormat df = new SimpleDateFormat("MMM d", Locale.getDefault());
        DateFormat df2 = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());
        return df.format(date) + " at " + df2.format(date);
    }
}
