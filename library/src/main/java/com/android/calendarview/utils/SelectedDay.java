package com.android.calendarview.utils;

import android.view.View;

import java.util.Calendar;

/**
 * This helper class represent a selected day when calendar is in a picker date mode.
 * It is used to remember a selected calendar cell.
 * <p>
 */

public class SelectedDay {
    private View mView;
    private View mViewCell;
    private Calendar mCalendar;

    public SelectedDay(Calendar calendar) {
        mCalendar = calendar;
    }

    /**
     * @param view     View representing selected calendar cell
     * @param calendar Calendar instance representing selected cell date
     */
    public SelectedDay(View view, View mViewCell, Calendar calendar) {
        mView = view;
        this.mViewCell = mViewCell;
        mCalendar = calendar;
    }

    /**
     * @return View representing selected calendar cell
     */
    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }

    /**
     * @return Calendar instance representing selected cell date
     */
    public Calendar getCalendar() {
        return mCalendar;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SelectedDay) {
            return getCalendar().equals(((SelectedDay) obj).getCalendar());
        }

        if (obj instanceof Calendar) {
            return getCalendar().equals(obj);
        }

        return super.equals(obj);
    }

    public View getViewCell() {
        return mViewCell;
    }

    public void setViewCell(View mViewCell) {
        this.mViewCell = mViewCell;
    }

    @Override
    public String toString() {
        return "SelectedDay{" +
                "mView=" + mView +
                ", mViewCell=" + mViewCell +
                ", mCalendar=" + mCalendar +
                '}';
    }
}

