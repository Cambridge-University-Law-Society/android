package com.camlawsocapp.culs.helpers;

import android.os.Build;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.List;

import androidx.annotation.RequiresApi;

public class Notification implements PostType, Comparable<PostType> {

    private String mNotificationTitle = "";
    private Date mDate = new Date(2007, 21, 5);
    private Timestamp mNotificationUST = new Timestamp(mDate);
    private String mNotificationSender = "";
    private List<String> mNotificationRecievers;
    private String mNotificationContent = "";
    private List<String> mPostRecievers;
    private String mNotificationType;
    private String mNotificationID = "";
    private boolean isShrink = true;
    private String mNotificationSenderName = "";


    public Notification(){}

    public Notification(String notificationTitle, Timestamp notificationUST, String notificationSender, List<String> notificationRecievers, String notificationContent, String notificationType, String notificationID, String notificationSenderName) {
        mNotificationTitle = notificationTitle;
        mNotificationUST = notificationUST;
        mNotificationSender = notificationSender;
        mNotificationRecievers = notificationRecievers;
        mNotificationContent = notificationContent;
        mNotificationType = notificationType;
        mNotificationID = notificationID;
        mNotificationSenderName = notificationSenderName;
    }

    public String getTitle() {
        return mNotificationTitle;
    }
    public Timestamp getTimestamp() { return mNotificationUST; }
    public String getSenderID() {
        return mNotificationSender;
    }
    public List<String> getRecieverID() { return mNotificationRecievers; }
    public String getContent() { return mNotificationContent; }
    public String getType() { return mNotificationType; }
    public String getNotificationID() { return mNotificationID; }
    public String getNotificationSenderName(){ return mNotificationSenderName; }

    public void setTitle(String mNotificationTitle) { this.mNotificationTitle = mNotificationTitle; }
    public void setTimestamp(Timestamp mNotificationUST) { this.mNotificationUST = mNotificationUST; }
    public void setSenderID(String mNotificationSender) { this.mNotificationSender = mNotificationSender; }
    public void setRecieverID(List<String> mNotificationReciever) { this.mNotificationRecievers = mNotificationReciever; }
    public void setContent(String mNotificationContent) { this.mNotificationContent = mNotificationContent; }
    public void setType(String mNotificationType) { this.mNotificationType = mNotificationType; }
    public void setNotificationID(String mNotificationID) { this.mNotificationID = mNotificationID; }
    public void setNotificationSenderName(String mNotificationSenderName) {
        this.mNotificationSenderName = mNotificationSenderName;
    }

    public boolean isShrink() {
        return isShrink;
    }

    public void setShrink(boolean shrink) {
        isShrink = shrink;
    }

    @Override
    public int getPostType() {
        return PostType.TYPE_NOTIFICATION;
    }

    @Override
    public long getTimediff() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(PostType postType) {
        return 0;
    }
}