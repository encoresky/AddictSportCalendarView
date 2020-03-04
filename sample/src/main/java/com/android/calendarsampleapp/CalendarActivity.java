package com.android.calendarsampleapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.calendarview.CalendarView;
import com.android.calendarview.EventDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

        List<EventDay> events = new ArrayList<>();


        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, 10);
        events.add(new EventDay(calendar1, R.drawable.ic_event_indicator));

        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setEvents(events);


    }

}
