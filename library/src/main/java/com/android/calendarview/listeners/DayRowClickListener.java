package com.android.calendarview.listeners;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.calendarview.CalendarUtils;
import com.android.calendarview.CalendarView;
import com.android.calendarview.EventDay;
import com.android.calendarview.R;
import com.android.calendarview.adapters.CalendarPageAdapter;
import com.android.calendarview.utils.CalendarProperties;
import com.android.calendarview.utils.DateUtils;
import com.android.calendarview.utils.DayColorsUtils;
import com.android.calendarview.utils.SelectedDay;
import com.annimon.stream.Stream;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This class is responsible for handle click events
 * <p>
 */

public class DayRowClickListener implements AdapterView.OnItemClickListener {

    private CalendarPageAdapter mCalendarPageAdapter;

    private CalendarProperties mCalendarProperties;
    private int mPageMonth;

    public DayRowClickListener(CalendarPageAdapter calendarPageAdapter, CalendarProperties calendarProperties, int pageMonth) {
        mCalendarPageAdapter = calendarPageAdapter;
        mCalendarProperties = calendarProperties;
        mPageMonth = pageMonth < 0 ? 11 : pageMonth;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Calendar day = new GregorianCalendar();
        day.setTime((Date) adapterView.getItemAtPosition(position));

        if (mCalendarProperties.getOnDayClickListener() != null) {
            onClick(day);
        }

        switch (mCalendarProperties.getCalendarType()) {
            case CalendarView.ONE_DAY_PICKER:
                selectOneDay(view, day);
                break;

            case CalendarView.MANY_DAYS_PICKER:
                selectManyDays(view, day);
                break;

            case CalendarView.RANGE_PICKER:
                selectRange(view, day);
                break;

            case CalendarView.CLASSIC:
                mCalendarPageAdapter.setSelectedDay(new SelectedDay(view, view, day));
        }
    }

    private void selectOneDay(View view, Calendar day) {
        SelectedDay previousSelectedDay = mCalendarPageAdapter.getSelectedDay();
        TextView dayLabel = view.findViewById(R.id.dayLabel);
        View dayCell = view.findViewById(R.id.dayCell);

        if (isAnotherDaySelected(previousSelectedDay, day)) {
            selectDay(dayLabel, dayCell, day);
            reverseUnselectedColor(previousSelectedDay);
        }
    }

    private void selectManyDays(View view, Calendar day) {
        TextView dayLabel = view.findViewById(R.id.dayLabel);
        View dayCell = view.findViewById(R.id.dayCell);

        if (isCurrentMonthDay(day) && isActiveDay(day)) {
            SelectedDay selectedDay = new SelectedDay(dayLabel, view, day);

            if (!mCalendarPageAdapter.getSelectedDays().contains(selectedDay)) {
                DayColorsUtils.setSelectedDayColors(dayLabel, dayCell, mCalendarProperties);
            } else {
                reverseUnselectedColor(selectedDay);
            }

            mCalendarPageAdapter.addSelectedDay(selectedDay);
        }
    }

    private void selectRange(View view, Calendar day) {
        TextView dayLabel = view.findViewById(R.id.dayLabel);
        View dayCell = view.findViewById(R.id.dayCell);

        if (!isCurrentMonthDay(day) || !isActiveDay(day)) {
            return;
        }

        List<SelectedDay> selectedDays = mCalendarPageAdapter.getSelectedDays();

        if (selectedDays.size() > 1) {
            clearAndSelectOne(dayLabel, dayCell, day);
        }

        if (selectedDays.size() == 1) {
            selectOneAndRange(dayLabel, dayCell, day);
        }

        if (selectedDays.isEmpty()) {
            selectDay(dayLabel, dayCell, day);
        }
    }

    private void clearAndSelectOne(TextView dayLabel, View dayCell, Calendar day) {
        Stream.of(mCalendarPageAdapter.getSelectedDays()).forEach(selectedDay -> reverseUnselectedColor(selectedDay));
        selectDay(dayLabel, dayCell, day);
    }

    private void selectOneAndRange(TextView dayLabel, View dayCell, Calendar day) {
        SelectedDay previousSelectedDay = mCalendarPageAdapter.getSelectedDay();

        Stream.of(CalendarUtils.getDatesRange(previousSelectedDay.getCalendar(), day))
                .filter(calendar -> !mCalendarProperties.getDisabledDays().contains(calendar))
                .forEach(calendar -> mCalendarPageAdapter.addSelectedDay(new SelectedDay(calendar)));

        if (isOutOfMaxRange(previousSelectedDay.getCalendar(), day)) {
            return;
        }

        DayColorsUtils.setSelectedDayColors(dayLabel, dayCell, mCalendarProperties);

        mCalendarPageAdapter.addSelectedDay(new SelectedDay(dayLabel, dayCell, day));
        mCalendarPageAdapter.notifyDataSetChanged();
    }

    private void selectDay(TextView dayLabel, View dayCell, Calendar day) {
        DayColorsUtils.setSelectedDayColors(dayLabel, dayCell, mCalendarProperties);
        mCalendarPageAdapter.setSelectedDay(new SelectedDay(dayLabel, dayCell, day));
    }

    private void reverseUnselectedColor(SelectedDay selectedDay) {
        DayColorsUtils.setCurrentMonthDayColors(selectedDay.getCalendar(), DateUtils.getCalendar(), (TextView) selectedDay.getView(), selectedDay.getViewCell(), mCalendarProperties);
    }

    private boolean isCurrentMonthDay(Calendar day) {
        return day.get(Calendar.MONTH) == mPageMonth && isBetweenMinAndMax(day);
    }

    private boolean isActiveDay(Calendar day) {
        return !mCalendarProperties.getDisabledDays().contains(day);
    }

    private boolean isBetweenMinAndMax(Calendar day) {
        return !((mCalendarProperties.getMinimumDate() != null && day.before(mCalendarProperties.getMinimumDate()))
                || (mCalendarProperties.getMaximumDate() != null && day.after(mCalendarProperties.getMaximumDate())));
    }

    private boolean isOutOfMaxRange(Calendar firstDay, Calendar lastDay) {

        // Number of selected days plus one last day
        int numberOfSelectedDays = CalendarUtils.getDatesRange(firstDay, lastDay).size() + 1;
        int daysMaxRange = mCalendarProperties.getMaximumDaysRange();

        return daysMaxRange != 0 && numberOfSelectedDays >= daysMaxRange;
    }

    private boolean isAnotherDaySelected(SelectedDay selectedDay, Calendar day) {
        return selectedDay != null && !day.equals(selectedDay.getCalendar())
                && isCurrentMonthDay(day) && isActiveDay(day);
    }

    private void onClick(Calendar day) {
        if (mCalendarProperties.getEventDays() == null) {
            createEmptyEventDay(day);
            return;
        }

        Stream.of(mCalendarProperties.getEventDays())
                .filter(eventDate -> eventDate.getCalendar().equals(day))
                .findFirst()
                .ifPresentOrElse(this::callOnClickListener, () -> createEmptyEventDay(day));
    }

    private void createEmptyEventDay(Calendar day) {
        EventDay eventDay = new EventDay(day);
        callOnClickListener(eventDay);
    }

    private void callOnClickListener(EventDay eventDay) {
        boolean enabledDay = mCalendarProperties.getDisabledDays().contains(eventDay.getCalendar())
                || !isBetweenMinAndMax(eventDay.getCalendar());

        eventDay.setEnabled(enabledDay);
        mCalendarProperties.getOnDayClickListener().onDayClick(eventDay);
    }
}
