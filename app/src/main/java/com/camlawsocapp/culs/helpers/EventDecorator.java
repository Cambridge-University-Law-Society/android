package com.camlawsocapp.culs.helpers;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class EventDecorator implements DayViewDecorator {

    private int color;
    private CalendarDay dates;

    public EventDecorator(int color, int year, int month, int day) {
        this.color = color;
        this.dates = CalendarDay.from(year, month, day);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        //return dates.contains(day);
        if (this.dates.equals(day)){
            return true;
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, color));
    }
}
