/*
 * Copyright (c) 2016, Sergey Penkovsky <sergey.penkovsky@gmail.com>
 *
 * This file is part of Erlymon Monitor.
 *
 * Erlymon Monitor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Erlymon Monitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Erlymon Monitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.erlymon.monitor.ui.widget;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.erlymon.monitor.R;

/**
 * Created by sergey on 08.04.17.
 */

public class DateTimePicker extends RelativeLayout {
    private AutoCompleteTextView date;
    private AutoCompleteTextView time;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    //private OnDateTimeClickListener mListener;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        calendar = Calendar.getInstance();

        View view = inflate(getContext(), R.layout.widget_date_time_picker, null);
        addView(view);

        //RelativeLayout relativeLayout = (RelativeLayout) getChildAt(0);
        GridLayout gridLayout = (GridLayout) getChildAt(0);
        //TextInputLayout textInputLayout = (TextInputLayout) gridLayout.getChildAt(0);

        date = (AutoCompleteTextView) gridLayout.findViewById(R.id.date);
        //textInputLayout = (TextInputLayout) gridLayout.getChildAt(1);
        time = (AutoCompleteTextView) gridLayout.findViewById(R.id.time);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        refreshUi();

        date.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        setDate(year, monthOfYear, dayOfMonth);
                    }

                }, getCalendar().get(Calendar.YEAR), getCalendar().get(Calendar.MONTH), getCalendar().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        setTime(hourOfDay, minute);
                    }
                }, getCalendar().get(Calendar.HOUR_OF_DAY), getCalendar().get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

    }
/*
    public void setOnDateTimeClickListener(OnDateTimeClickListener onDateTimeClickListener) {
        mListener = onDateTimeClickListener;
    }
*/
    public Calendar getCalendar() {
        return calendar;
    }

    public Date getTime() {
        return calendar.getTime();
    }

    public void setDate(int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        refreshUi();
    }

    public void setTime(int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        refreshUi();
    }

    private void refreshUi() {
        String[] params = simpleDateFormat.format(calendar.getTime()).split(" ");

        date.setText(params[0]);
        time.setText(params[1]);
    }
    /*
    public interface OnDateTimeClickListener {
        void onDateClicked(View view);

        void onTimeClicked(View view);
    }
    */
}
