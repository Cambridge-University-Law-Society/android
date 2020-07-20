package com.example.culs.helpers;

import android.media.Image;

import java.util.ArrayList;

public class User {

    private String firstname;
    private String lastname;
    private String college;
    private String year;
    private String crsid;
    private String uid;
    private ArrayList<String> myevents;
    private String bio;
    private String status;
    private Image profilePic;

    public User() {
    }

    public User(String firstname, String lastname, String college, String year, String crsid, String uid, ArrayList<String> myevents, String bio, String status, Image profilePic) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.college = college;
        this.year = year;
        this.crsid = crsid;
        this.uid = uid;
        this.myevents = myevents;
        this.bio = bio;
        this.status = status;
        this.profilePic = profilePic;
    }

    public String getFirstname() {
        return firstname;
    }

    /*public void setFirst_name(String first_name) {
        this.firstname = firstname;
    }*/

    public String getLastname() {
        return lastname;
    }

    /*public void setLast_name(String last_name) {
        this.lastname = lastname;
    }*/

    public String getCollege() {
        return college;
    }

    /*public void setCollege(String college) {
        this.college = college;
    }*/

    public String getYear() {
        return year;
    }

    /*public void setYear(String year) {
        this.year = year;
    }*/

    public String getCrsid() {
        return crsid;
    }

    /*public void setCrsid(String crsid) {
        this.crsid = crsid;
    }*/

    public String getUid() {
        return uid;
    }

    /*public void setUid(String uid) {
        this.uid = uid;
    }*/

    public ArrayList<String> getMyevents() {
        return myevents;
    }

    /*public void setMyevents(ArrayList<String> myevents) {
        this.myevents = myevents;
    }*/

    public String getBio() {
        return bio;
    }

    /*public void setBio(String bio) {
        this.bio = bio;
    }*/

    public String getStatus() {
        return status;
    }

    /*public void setStatus(String status) {
        this.status = status;
    }*/

    public Image getProfilePic() {
        return profilePic;
    }

    /*public void setProfilePic(Image profilePic) {
        this.profilePic = profilePic;
    }*/
}
