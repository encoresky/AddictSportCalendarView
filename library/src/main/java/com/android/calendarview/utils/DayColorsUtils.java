package com.android.calendarview.utils;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.android.calendarview.R;

import java.util.Calendar;

/**
 * This class is used to set a style of calendar cells.
 * <p>
 */

public class DayColorsUtils {


    /**
     * This is general method which sets a color of the text, font type and a background of a TextView object.
     * It is used to set day cell (numbers) style.
     *
     * @param textView   TextView containing a day number
     * @param textColor  A resource of a color of the day number
     * @param typeface   A type of text style, can be set as NORMAL or BOLD
     * @param background A resource of a background drawable
     */
    public static void setDayColors(TextView textView, View dayCell, int textColor, int typeface, int background) {
        if (textView == null) {
            return;
        }
        textView.setTypeface(null, typeface);
        textView.setTextColor(textColor);
        System.out.println("setDayColors");
        dayCell.setBackgroundResource(background);
    }

    /**
     * This method sets a color of the text, font type and a background of a TextView object.
     * It is used to set day cell (numbers) style in the case of selected day (when calendar is in
     * the picker mode). It also colors a background of the selection.
     *
     * @param dayLabel           TextView containing a day number
     * @param calendarProperties A resource of a selection background color
     */
    public static void setSelectedDayColors(TextView dayLabel, View dayCell, CalendarProperties calendarProperties) {
        System.out.println("setSelectedDayColors " + dayCell);
        setDayColors(dayLabel, dayCell, calendarProperties.getDaysLabelsColor(), Typeface.NORMAL, R.drawable.background_color_circle_selector);
        setDayBackgroundColor(dayCell, calendarProperties.getSelectionColor());
    }

    /**
     * This method is used to set a color of texts, font types and backgrounds of TextView objects
     * in a current visible month. Visible day labels from previous and forward months are set using
     * setDayColors() method. It also checks if a day number is a day number of today and set it
     * a different color and bold face type.
     *
     * @param day                A calendar instance representing day date
     * @param today              A calendar instance representing today date
     * @param dayLabel           TextView containing a day numberx
     * @param calendarProperties A resource of a color used to mark today day
     */
    public static void setCurrentMonthDayColors(Calendar day, Calendar today, TextView dayLabel, View dayCell, CalendarProperties calendarProperties) {
        System.out.println("setCurrentMonthDayColors " + dayCell);
        if (today.equals(day)) {
            setTodayColors(dayLabel, dayCell, calendarProperties);
        } else if (EventDayUtils.isEventDayWithLabelColor(day, calendarProperties)) {
            setEventDayColors(day, dayLabel, dayCell, calendarProperties);
        } else if (calendarProperties.getHighlightedDays().contains(day)) {
            setHighlightedDayColors(dayLabel, dayCell, calendarProperties);
        } else {
            setNormalDayColors(dayLabel, dayCell, calendarProperties);
        }
    }

    private static void setTodayColors(TextView dayLabel, View dayCell, CalendarProperties calendarProperties) {
        System.out.println("setTodayColors " + dayCell);
        setDayColors(dayLabel, dayCell, calendarProperties.getTodayLabelColor(), Typeface.BOLD, R.drawable.background_transparent);
        // Sets custom background color for present
        if (calendarProperties.getTodayColor() != 0) {
            setDayColors(dayLabel, dayCell, calendarProperties.getSelectionLabelColor(), Typeface.NORMAL, R.drawable.background_color_circle_selector);
            setDayBackgroundColor(dayCell, calendarProperties.getTodayColor());
        }
    }

    private static void setEventDayColors(Calendar day, TextView dayLabel, View dayCell, CalendarProperties calendarProperties) {
        System.out.println("setEventDayColors " + dayCell);
        EventDayUtils.getEventDayWithLabelColor(day, calendarProperties).executeIfPresent(eventDay ->
                DayColorsUtils.setDayColors(dayLabel, dayCell, eventDay.getLabelColor(), Typeface.NORMAL, R.drawable.background_transparent));
    }

    private static void setHighlightedDayColors(TextView dayLabel, View dayCell, CalendarProperties calendarProperties) {
        System.out.println("setHighlightedDayColors " + dayCell);
        setDayColors(dayLabel, dayCell, calendarProperties.getHighlightedDaysLabelsColor(), Typeface.NORMAL, R.drawable.background_transparent);
    }

    private static void setNormalDayColors(TextView dayLabel, View dayCell, CalendarProperties calendarProperties) {
        System.out.println("setNormalDayColors " + dayCell);
        setDayColors(dayLabel, dayCell, calendarProperties.getDaysLabelsColor(), Typeface.NORMAL, R.drawable.background_transparent);
    }

    private static void setDayBackgroundColor(View dayCell, int color) {
        System.out.println("setDayBackgroundColor " + dayCell);
        dayCell.getBackground().setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
    }
}
