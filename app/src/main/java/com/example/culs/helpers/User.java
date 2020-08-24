package com.example.culs.helpers;

import android.media.Image;

import java.util.ArrayList;

public class User {

    private String mFirstname;
    private String mLastname;
    private String mCollege;
    private String mYear;
    private String mCrsid;
    private String mUid;
    private ArrayList<String> mMyevents;
    private String mBio;
    private String mStatus;
    private String mDegree;
    private String mProfilePicRef;

    public User() {
    }

    public User(String firstname, String lastname, String college, String year, String crsid, String uid, ArrayList<String> myevents, String bio, String status, String degree, String profilePicRef) {

        mFirstname = firstname;
        mLastname = lastname;
        mCollege = college;
        mYear = year;
        mCrsid = crsid;
        mUid = uid;
        mMyevents = myevents;
        mBio = bio;
        mStatus = status;
        mDegree = degree;
        mProfilePicRef = profilePicRef;
    }

    public String getFirstname() {
        return mFirstname;
    }
    public String getLastname() {
        return mLastname;
    }
    public String getCollege() {
        return mCollege;
    }
    public String getYear() {
        return mYear;
    }
    public String getCrsid() {
        return mCrsid;
    }
    public String getUid() {
        return mUid;
    }
    public String getBio() {
        return mBio;
    }
    public ArrayList<String> getMyevents() {
        return mMyevents;
    }
    public String getStatus() {
        return mStatus;
    }
    public String getDegree() {
        return mDegree;
    }

    public void setFirstname(String firstname) {
        this.mFirstname = firstname;
    }
    public void setLastname(String lastname) {
        this.mLastname = lastname;
    }
    public void setCollege(String college) {
        this.mCollege = college;
    }
    public void setYear(String year) {
        this.mYear = year;
    }
    public void setCrsid(String crsid) {
        this.mCrsid = crsid;
    }
    public void setUid(String uid) {
        this.mUid = uid;
    }
    public void setMyevents(ArrayList<String> myevents) {
        this.mMyevents = myevents;
    }
    public void setBio(String bio) {
        this.mBio = bio;
    }
    public void setStatus(String status) {
        this.mStatus = status;
    }
    public void setDegree(String degree) {
        this.mDegree = degree;
    }

}
