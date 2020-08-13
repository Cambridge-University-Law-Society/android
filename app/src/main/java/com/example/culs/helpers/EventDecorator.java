package com.example.culs.helpers;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.threeten.bp.LocalDate;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {

    private int color;
    private CalendarDay day;

    //a Collection represents a group of objects/ elements

    public EventDecorator(int color, LocalDate date){
        this.color = color;
        this.day = CalendarDay.from(date);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        if (this.day.equals(day)){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void decorate(DayViewFacade view) {
        //decorate the event days
        view.addSpan(new DotSpan(5, color));
    }
}
