package com.example.culs.helpers;

public class InterestsModel {

    String interests;
    private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public InterestsModel(String interests) {
        this.interests = interests;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }
}
