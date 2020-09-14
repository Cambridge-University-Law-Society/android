package com.example.culs.helpers;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;
import java.util.List;
import com.google.firebase.Timestamp;

import androidx.annotation.RequiresApi;


public class Card implements PostType, Comparable<PostType>{

    private String mEventName;
    private Timestamp mEventUST;
    private String mEventLocation;
    private String mEventDescription;
    private String mEventImageURL;
    private List<String> mEventTags;
    private String mEventSponsor;
    private String mEventID;
    private Boolean mEventInterested = false;
    private String mEventSponsorID;

    public Card(){}

    public Card(String eventName, Timestamp eventUST, String eventLocation, String eventDescription, String eventImageURL, List<String> eventTags, String eventSponsor, String eventID, Boolean eventInterested, String eventSponsorID, Boolean eventRefreshing, Long eventLastUpdated) {
        mEventName = eventName;
        mEventUST = eventUST;
        mEventLocation = eventLocation;
        mEventDescription = eventDescription;
        mEventImageURL = eventImageURL;
        mEventTags = eventTags;
        mEventSponsor = eventSponsor;
        mEventID = eventID;
        mEventInterested = eventInterested;
        mEventSponsorID = eventSponsorID;

    }

    public String getID() { return mEventID; }
    public String getName() {
        return mEventName;
    }
    public Timestamp getDate() { return mEventUST; }
    public String getLocation() {
        return mEventLocation;
    }
    public String getDescription() { return mEventDescription; }
    public String getImageURL() { return mEventImageURL; }
    public List<String> getTags() { return mEventTags; }
    public String getSponsor() { return mEventSponsor; }
    public Boolean getInterested() { return mEventInterested; }
    public String getEventSponsorID() { return mEventSponsorID; }

    public void setID(String mEventID) { this.mEventID = mEventID; }
    public void setName(String mEventName) { this.mEventName = mEventName; }
    public void setDate(Timestamp mEventUST) { this.mEventUST = mEventUST; }
    public void setLocation(String mEventLocation) { this.mEventLocation = mEventLocation; }
    public void setImageURL(String mEventImageURL) { this.mEventImageURL = mEventImageURL; }
    public void setDescription(String mEventDescription) { this.mEventDescription = mEventDescription; }
    public void setTags(List<String> mEventTags) { this.mEventTags = mEventTags; }
    public void setSponsor(String mEventSponsor) { this.mEventSponsor = mEventSponsor; }
    public void setInterested(Boolean mEventInterested) { this.mEventInterested = mEventInterested; }
    public void setEventSponsorID(String mEventSponsorID) { this.mEventSponsorID = mEventSponsorID; }


    @Override
    public int getPostType() {
        return PostType.TYPE_EVENT;
    }

    @Override
    public long getTimediff() {
        long now = (System.currentTimeMillis()/1000);
        long then = (mEventUST.toDate().getTime()/1000);
        return(Math.abs(then - now));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(PostType postType) {
        return Long.compare(getTimediff(), postType.getTimediff());
    }
}