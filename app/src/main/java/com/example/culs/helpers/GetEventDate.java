package com.example.culs.helpers;

import java.util.ArrayList;
import java.util.Date;

public class GetEventDate {

    private int mYear;
    private int mMonth;
    private int mDay;
    private Date mDate;
    private ArrayList<Date> dateTime = new ArrayList<Date>();

    public GetEventDate(){
    }

    public GetEventDate(Date date){
        //mYear = year;
        //mMonth = month;
        //mDay = day;
        this.mDate = date;
    }

    //create setters and getters

    public Date getmDate() {
        return mDate;
    }


}
