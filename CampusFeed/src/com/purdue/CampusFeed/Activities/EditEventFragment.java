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
import com.purdue.CampusFeed.AsyncTasks.ModifyEvent;
import com.purdue.CampusFeed.R;

import java.util.Calendar;

/**
 * Created by Sean on 3/8/14.
 */
public class EditEventFragment extends Fragment {
    private static final String[] tagSuggestions = new String[]{"Social", "Cultural", "Education"};
    public static int year, month, day, hour, minute, visibility;
    /*Called when the user finishes selecting a date from the dialog*/
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedMonth;

            //Change spinner's text view to selected date
            dateSpinner.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year).append(" "));
        }
    };
    public static String title, description, location, date_time;
    public static String[] categories;
    public Event myEvent;
    public EditText dateSpinner, timeSpinner;
    EditText nameText, descriptionText, locationText;
    MultiAutoCompleteTextView multiAutoCompleteTextView;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Button doneButton;
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

    public static EditEventFragment create(Event event) {
        EditEventFragment frag = new EditEventFragment();
        frag.myEvent = event;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_event, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nameText = (EditText) getActivity().findViewById(R.id.nameText);
        descriptionText = (EditText) getActivity().findViewById(R.id.descriptionText);
        locationText = (EditText) getActivity().findViewById(R.id.locationText);
        dateSpinner = (EditText) getActivity().findViewById(R.id.dateSpinner);
        timeSpinner = (EditText) getActivity().findViewById(R.id.timeSpinner);
        doneButton = (Button) getActivity().findViewById(R.id.done);
        multiAutoCompleteTextView = (MultiAutoCompleteTextView) getActivity().findViewById(R.id.tagText);
        multiAutoCompleteTextView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, tagSuggestions));
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        //set default text field values
        nameText.setText(myEvent.getEventName());
        descriptionText.setText(myEvent.getEventDescription());
        locationText.setText(myEvent.getEventLocation());

        //get current date information from our event
        String[] date = myEvent.getDatetime().split(" ");

        //get current date
        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);


        //get current time
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        //set default prompt for dateSpinner
        dateSpinner.setText(date[0]);
        //set default prompt for timeSpinner
        timeSpinner.setText(date[1]);

        datePickerDialog = new DatePickerDialog(getActivity(), datePickerListener, year, month, day);
        timePickerDialog = new TimePickerDialog(getActivity(), timePickerListener, hour, minute, true);
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

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameText = (EditText) getActivity().findViewById(R.id.nameText);
                EditText descriptionText = (EditText) getActivity().findViewById(R.id.descriptionText);
                EditText locationText = (EditText) getActivity().findViewById(R.id.locationText);

                //update event data structure
                myEvent.setEventName(nameText.getText().toString());
                myEvent.setEventDescription(descriptionText.getText().toString());
                myEvent.setEventLocation(locationText.getText().toString());
                myEvent.setDatetime("" + month + "-" + day + "-" + year + " " + hour + ":" + minute);
                title = myEvent.getEventName();
                description = myEvent.getEventDescription();
                location = myEvent.getEventLocation();
                date_time = myEvent.getDatetime();
                visibility = ((RadioButton) EditEventFragment.this.getActivity().findViewById(R.id.privateEvent)).isChecked() ? Event.PRIVATE : Event.PUBLIC;
                categories = multiAutoCompleteTextView.getText().toString().split(",");
                for (int i = 0; i < categories.length; i++)
                    categories[i] = categories[i].replace(" ", "");
                new ModifyEvent(myEvent, EditEventFragment.this.getActivity()).execute("fsdf");
                Toast.makeText(getActivity(), "Event updated to: " + myEvent.getEventLocation() + "" + myEvent.getDatetime(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}