package com.camlawsocapp.culs.helpers;

public class Sponsor implements PostType {

    private String mSponsorName;
    private String mSponsorDescription;
    private String mSponsorWebsite;
    private String mSponsorID;
    private String mSponsorApplicationPage;
    private String mSponsorCSLink;
    private String mSponsorEmail;

    public Sponsor(){}

    public Sponsor(String sponsorName, String sponsorDescription, String sponsorID, String sponsorWebsite, String sponsorApplicationPage, String sponsorCSLink, String sponsorEmail) {
        mSponsorName = sponsorName;
        mSponsorDescription = sponsorDescription;
        mSponsorWebsite = sponsorWebsite;
        mSponsorID = sponsorID;
        mSponsorApplicationPage = sponsorApplicationPage;
        mSponsorCSLink = sponsorCSLink;
        mSponsorEmail = sponsorEmail;
    }

    public String getName() {
        return mSponsorName;
    }
    public String getBio() {
        return mSponsorDescription;
    }
    public String getWebsite() { return mSponsorWebsite; }
    public String getSponsorID() { return mSponsorID; }
    public String getApplicationpage() { return mSponsorApplicationPage; }
    public String getCslink() { return mSponsorCSLink; }
    public String getEmail() { return mSponsorEmail; }

    public void setName(String mSponsorName) { this.mSponsorName = mSponsorName; }
    public void setBio(String mSponsorDescription) { this.mSponsorDescription = mSponsorDescription; }
    public void setWebsite(String mSponsorURL) { this.mSponsorWebsite = mSponsorURL; }
    public void setSponsorID(String mSponsorID) { this.mSponsorID = mSponsorID; }
    public void setApplicationpage(String mSponsorApplicationPage) { this.mSponsorApplicationPage = mSponsorApplicationPage; }
    public void setCslink(String mSponsorCSLink) { this.mSponsorCSLink = mSponsorCSLink; }
    public void setEmail(String mSponsorEmail) { this.mSponsorEmail = mSponsorEmail; }

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