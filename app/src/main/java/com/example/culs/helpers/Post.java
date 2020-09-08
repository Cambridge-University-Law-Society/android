package com.example.culs.helpers;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

import com.google.firebase.Timestamp;

import androidx.annotation.RequiresApi;


public class Post implements PostType, Comparable<PostType> {

    private String mPostTitle = "";
    private Date mDate = new Date(2007, 21, 5);
    private Timestamp mPostUST = new Timestamp(mDate);
    private String mPostSender = "";
    private String mPostSenderName = "";
    private String mPostContent = "";
    private List<String> mPostRecievers;
    private String mPostID = "";
    private boolean isShrink = true;


    public Post(){}

    public Post(String postTitle, Timestamp postUST, String postSender, String postContent, String postID, String postSenderName) {
        mPostTitle = postTitle;
        mPostUST = postUST;
        mPostSender = postSender;
        mPostContent = postContent;
        mPostID = postID;
        mPostSenderName = postSenderName;
    }

    public String getTitle() {
        return mPostTitle;
    }
    public Timestamp getTimestamp() { return mPostUST; }
    public String getSenderID() {
        return mPostSender;
    }
    public String getContent() { return mPostContent; }
    public String getPostID() { return mPostID; }
    public String getSenderName() {return mPostSenderName;}

    public void setTitle(String mPostTitle) { this.mPostTitle = mPostTitle; }
    public void setTimestamp(Timestamp mPostUST) { this.mPostUST = mPostUST; }
    public void setSenderID(String mPostSender) { this.mPostSender = mPostSender; }
    public void setContent(String mPostContent) { this.mPostContent = mPostContent; }
    public void setPostID(String mPostID) { this.mPostID = mPostID; }
    public void setSenderName(String mPostSenderName) { this.mPostSenderName = mPostSenderName; }

    public boolean isShrink() {
        return isShrink;
    }

    public void setShrink(boolean shrink) {
        isShrink = shrink;
    }

    @Override
    public int getPostType() {
        return PostType.TYPE_POST;
    }

    @Override
    public long getTimediff() {
        long now = (System.currentTimeMillis()/1000);
        long then = (mPostUST.toDate().getTime()/1000);
        return(Math.abs(now - then));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(PostType postType) {
        return Long.compare(getTimediff(), postType.getTimediff());
    }
}
