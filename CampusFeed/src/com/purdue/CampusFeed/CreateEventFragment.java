package com.purdue.CampusFeed;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Sean on 2/27/14.
 */
public class CreateEventFragment extends Fragment {
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private EditText dateSpinner;
    private EditText timeSpinner;
    private static int year;
    private static int month;
    private static int day;
    private static int hour;
    private static int minute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dateSpinner = (EditText) getActivity().findViewById(R.id.dateSpinner);
        timeSpinner = (EditText) getActivity().findViewById(R.id.timeSpinner);
        //get current date
        final Calendar cal = Calendar.getInstance();
        year  = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day   = cal.get(Calendar.DAY_OF_MONTH);

        //get current time
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        //set default prompt for dateSpinner
        dateSpinner.setText("Today");
        //set default prompt for timeSpinner
        timeSpinner.setText(""+hour+":"+minute);

        datePickerDialog = new DatePickerDialog(getActivity(), datePickerListener, year, month, day);
        timePickerDialog = new TimePickerDialog(getActivity(), timePickerListener, hour, minute, false);
        /*Called when dateSpinner is clicked*/
        dateSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show date picker
                datePickerDialog.show();
            }
        });
        timeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });
        return inflater.inflate(R.layout.create_event, container, false);
    }

    /*Called when the user finishes selecting a date from the dialog*/
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedMonth;

            //Change spinner's text view to selected date
            dateSpinner.setText(new StringBuilder().append(month+1)
                        .append("-").append(day).append("-").append(year).append(" "));
        }
    };

    /*Called when the user finishes selecting the time of the event*/
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            hour = selectedHour;
            minute = selectedMinute;

            //update spinner text
            timeSpinner.setText(new StringBuilder().append(hour).append(":").append(minute));
        }
    };
}