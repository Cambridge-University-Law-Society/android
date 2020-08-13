package com.example.culs.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.culs.R;
import com.google.type.Color;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarFragment extends Fragment {
    @Nullable

    // Define the variable of CalendarView type and TextView type
    TextView date_view;
    CalendarView calender;
    private SimpleDateFormat dataFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        // CalendarView and TextView
        calender = (CalendarView) v.findViewById(R.id.calender);
        date_view = (TextView) v.findViewById(R.id.date_view);

        // Add Listener in calendar
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override

            // In this Listener have one method and in this method we will get the value of DAYS, MONTH, YEARS
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Store the value of date with format in String type Variable
                // Add 1 in month because month index is start with 0
                String Date = dayOfMonth + "/" + (month + 1) + "/" + year;

                // set this date in TextView for Display
                date_view.setText(Date);
            }
        });

        Event ev1 = new Event(Color.RED_FIELD_NUMBER,  1596466053000L, "Some event");
        return v;
    }
    

}
