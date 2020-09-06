package com.example.culs.helpers;

import java.util.List;

public class Sponsor implements PostType {

    private String mSponsorName;
    private String mSponsorDescription;
    private String mSponsorURL;
    private List<String> mSponsorLinks;
    private String mSponsorTypes;
    private String mSponsorID;

    public Sponsor(){}

    public Sponsor(String sponsorName, String sponsorDescription, String SponsorID, String sponsorTypes, String sponsorURL, List<String> sponsorLinks) {
        mSponsorName = sponsorName;
        mSponsorDescription = sponsorDescription;
        mSponsorURL = sponsorURL;
        mSponsorLinks = sponsorLinks;
        mSponsorID = SponsorID;
        mSponsorTypes = sponsorTypes;
    }


    public String getName() {
        return mSponsorName;
    }
    public String getBio() {
        return mSponsorDescription;
    }
    public String getWebsite() { return mSponsorURL; }
    public List<String> getRelatedLinks() {
        return mSponsorLinks;
    }
    public String getTypes() { return mSponsorTypes; }
    public String getSponsorID() { return mSponsorID; }

    public void setName(String mSponsorName) { this.mSponsorName = mSponsorName; }
    public void setBio(String mSponsorDescription) { this.mSponsorDescription = mSponsorDescription; }
    public void setWebsite(String mSponsorURL) { this.mSponsorURL = mSponsorURL; }
    public void setRelatedLinks(List<String> mSponsorLinks) { this.mSponsorLinks = mSponsorLinks; }
    public void setTypes(String mSponsorTypes) { this.mSponsorTypes = mSponsorTypes; }
    public void setSponsorID(String mSponsorID) { this.mSponsorID = mSponsorID; }

    @Override
    public int getPostType() {
        return TYPE_SPONSOR;
    }

    @Override
    public long getTimediff() {
        return 0;
    }

    @Override
    public int compareTo(PostType postType) {
        return 0;
    }
}