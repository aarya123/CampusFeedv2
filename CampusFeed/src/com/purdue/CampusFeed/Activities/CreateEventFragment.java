package com.purdue.CampusFeed.Activities;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.AsyncTasks.CreateEvent;
import com.purdue.CampusFeed.R;

import java.util.Calendar;

/**
 * Created by Sean on 2/27/14.
 */
public class CreateEventFragment extends Fragment {
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    EditText dateSpinner, timeSpinner, nameText, descriptionText, locationText;
    int year, month, day, hour, minute;
    private Button doneButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_event, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dateSpinner = (EditText) getActivity().findViewById(R.id.dateSpinner);
        timeSpinner = (EditText) getActivity().findViewById(R.id.timeSpinner);
        doneButton = (Button) getActivity().findViewById(R.id.done);
        nameText = (EditText) getActivity().findViewById(R.id.nameText);
        descriptionText = (EditText) getActivity().findViewById(R.id.descriptionText);
        locationText = (EditText) getActivity().findViewById(R.id.locationText);
        // get current date
        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        // get current time
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        // set default prompt for dateSpinner
        dateSpinner.setText("Today");
        // set default prompt for timeSpinner
        timeSpinner.setText("" + hour + ":" + minute);
        datePickerDialog = new DatePickerDialog(getActivity(), datePickerListener, year, month, day);
        timePickerDialog = new TimePickerDialog(getActivity(), timePickerListener, hour, minute, true);
        /* Called when dateSpinner is clicked */
        dateSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show date picker
                datePickerDialog.show();
            }
        });
        timeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = nameText.getText().toString();
                String description = descriptionText.getText().toString();
                String location = locationText.getText().toString();
                String time_start = "" + (month + 1) + "-" + day + "-" + year + " " + hour + ":" + minute;
                int visibility = ((RadioButton) CreateEventFragment.this.getActivity().findViewById(R.id.privateEvent)).isChecked() ? Event.PRIVATE : Event.PUBLIC;
                new CreateEvent(getActivity()).execute(new Event(title, description, location, time_start, new String[]{"Social"}, visibility));
                Toast.makeText(getActivity(), "event created", Toast.LENGTH_LONG).show();
            }
        });
    }

    /* Called when the user finishes selecting a date from the dialog */
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedMonth;

            // Change spinner's text view to selected date
            dateSpinner.setText(new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" "));
        }
    };

    /* Called when the user finishes selecting the time of the event */
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour,
                              int selectedMinute) {
            hour = selectedHour;
            minute = selectedMinute;

            // update spinner text
            timeSpinner.setText(new StringBuilder().append(hour).append(":")
                    .append(minute));
        }
    };
}

