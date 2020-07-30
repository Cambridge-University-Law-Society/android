package com.example.culs.helpers;


import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;


public class Sponsor implements Parcelable {

    private String mSponsorName;
    private String mSponsorDescription;
    private String mSponsorURL;
    private String mSponsorLinks[];
    private String mSponsorTypes;
    private int mSponsorLogo = NO_IMAGE_PROVIDED;
    private static final int NO_IMAGE_PROVIDED = -1;


    public Sponsor(String sponsorName, String sponsorDescription, int sponsorLogo, String sponsorTypes, String sponsorURL, String sponsorLinks[]) {
        mSponsorName = sponsorName;
        mSponsorDescription = sponsorDescription;
        mSponsorURL = sponsorURL;
        mSponsorLinks = sponsorLinks;
        mSponsorLogo = sponsorLogo;
        mSponsorTypes = sponsorTypes;
    }

    public static final Creator<Sponsor> CREATOR = new Creator<Sponsor>() {
        @Override
        public Sponsor createFromParcel(Parcel in) {
            return new Sponsor(in);
        }

        @Override
        public Sponsor[] newArray(int size) {
            return new Sponsor[size];
        }
    };

    public Sponsor(Parcel in) {
        mSponsorName = in.readString();
        mSponsorDescription = in.readString();
        mSponsorLogo = in.readInt();
        mSponsorTypes = in.readString();
        mSponsorURL = in.readString();
        mSponsorLinks = in.createStringArray();

    }

    public String getSponsorName() {
        return mSponsorName;
    }
    public String getSponsorDescription() {
        return mSponsorDescription;
    }
    public String getSponsorURL() { return mSponsorURL; }
    public String[] getSponsorLinks() {
        return mSponsorLinks;
    }
    public int getSponsorLogo() { return mSponsorLogo; }
    public String getSponsorTypes() { return mSponsorTypes; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mSponsorName);
        parcel.writeString(mSponsorDescription);
        parcel.writeString(mSponsorURL);
        parcel.writeStringArray(mSponsorLinks);
        parcel.writeString(mSponsorTypes);
        parcel.writeInt(mSponsorLogo);

    }
}