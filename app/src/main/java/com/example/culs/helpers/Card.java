package com.example.culs.helpers;


import android.os.Parcel;
import android.os.Parcelable;


public class Card implements Parcelable {

    private String mEventName;
    private Long mEventUST;
    private String mEventDateAndTime;
    private String mEventLocation;
    private String mEventFriends[];
    private int mEventFriendPics[];
    private String mEventImage;
    private String mEventDescription;
    private Boolean mEventTags[];

//    public Card(String eventName, String eventDateAndTime, Long eventUST, String eventLocation, String eventDescription) {
//        mEventName = eventName;
//        mEventDateAndTime = eventDateAndTime;
//        mEventUST = eventUST;
//        mEventLocation = eventLocation;
//        mEventDescription = eventDescription;
//    }
//
//    public Card(String eventName, String eventDateAndTime, Long eventUST, String eventLocation, String eventDescription, int eventImage, Boolean[] eventTags) {
//        mEventName = eventName;
//        mEventDateAndTime = eventDateAndTime;
//        mEventUST = eventUST;
//        mEventLocation = eventLocation;
//        mEventDescription = eventDescription;
//        mEventImage = eventImage;
//        mEventTags = eventTags;
//    }

    public Card(String eventName, String eventDateAndTime, Long eventUST, String eventLocation, String eventDescription, String eventImage, String[] eventFriends, int[] eventFriendPics, Boolean[] eventTags) {
        mEventName = eventName;
        mEventDateAndTime = eventDateAndTime;
        mEventUST = eventUST;
        mEventLocation = eventLocation;
        mEventDescription = eventDescription;
        mEventImage = eventImage;
        mEventFriends = eventFriends;
        mEventFriendPics = eventFriendPics;
        mEventTags = eventTags;
    }

    protected Card(Parcel in) {
        mEventName = in.readString();
        mEventUST = in.readLong();
        mEventDateAndTime = in.readString();
        mEventLocation = in.readString();
        mEventFriends = in.createStringArray();
        mEventFriendPics = in.createIntArray();
        mEventImage = in.readString();
        mEventDescription = in.readString();
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

    public String getEventName() {
        return mEventName;
    }
    public Long getEventUST() { return mEventUST; }
    public String getEventDateAndTime() {
        return mEventDateAndTime;
    }
    public String getEventLocation() {
        return mEventLocation;
    }
    public String getEventDescription() { return mEventDescription; }
    public String getEventImage() { return mEventImage; }
    public String[] getEventFriends() { return mEventFriends; }
    public int[] getEventFriendPics() { return mEventFriendPics; }
    public Boolean[] getEventTags() { return mEventTags; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mEventName);
        parcel.writeLong(mEventUST);
        parcel.writeString(mEventDateAndTime);
        parcel.writeString(mEventLocation);
        parcel.writeStringArray(mEventFriends);
        parcel.writeIntArray(mEventFriendPics);
        parcel.writeString(mEventImage);
        parcel.writeString(mEventDescription);
    }
}