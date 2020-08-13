package com.example.culs.helpers;


import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;
import java.util.List;

import com.google.firebase.Timestamp;


public class Card implements Parcelable {

    private String mEventName;
    private Timestamp mEventUST;
    private Date mEventDate;
    private Long mEventDateLong;
    private String mEventLocation;
    private String mEventDescription;
    private String mEventImageURL;
    private List<String> mEventTags;
    private String mEventSponsor;

    public Card(){}

    public Card(String eventName, Timestamp eventUST, String eventLocation, String eventDescription, String eventImageURL, List<String> eventTags, String eventSponsor) {
        mEventName = eventName;
        mEventUST = eventUST;
        mEventDate = eventUST.toDate();
        mEventDateLong = eventUST.toDate().getTime();
        mEventLocation = eventLocation;
        mEventDescription = eventDescription;
        mEventImageURL = eventImageURL;
        mEventTags = eventTags;
        mEventSponsor = eventSponsor;
    }

    protected Card(Parcel in) {
        mEventName = in.readString();
        mEventDateLong = in.readLong();
        mEventLocation = in.readString();
        mEventImageURL = in.readString();
        mEventDescription = in.readString();
        mEventTags = in.createStringArrayList();
        mEventSponsor = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mEventName);
        parcel.writeLong(mEventDateLong);
        parcel.writeString(mEventLocation);
        parcel.writeString(mEventDescription);
        parcel.writeString(mEventImageURL);
        parcel.writeString(mEventSponsor);
        parcel.writeStringList(mEventTags);
    }

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

    public void setName(String mEventName) { this.mEventName = mEventName; }
    public void setDate(Timestamp mEventUST) { this.mEventUST = mEventUST; }
    public void setLocation(String mEventLocation) { this.mEventLocation = mEventLocation; }
    public void setImageURL(String mEventImageURL) { this.mEventImageURL = mEventImageURL; }
    public void setDescription(String mEventDescription) { this.mEventDescription = mEventDescription; }
    public void setTags(List<String> mEventTags) { this.mEventTags = mEventTags; }
    public void setSponsor(String mEventSponsor) { this.mEventSponsor = mEventSponsor; }
}