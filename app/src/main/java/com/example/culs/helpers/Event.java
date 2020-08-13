package com.example.culs.helpers;

import com.google.firebase.Timestamp;

import java.util.List;

public class Event {

    public Boolean active;
    public String imageRef;
    public Timestamp date;
    public String description;
    public String location;
    public String eventName;
    public String sponsorName;
    public List<String> tags;

    public Event(){

    }

}