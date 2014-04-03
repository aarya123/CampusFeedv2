package com.purdue.CampusFeed.Activities;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.AsyncTasks.ModifyEvent;
import com.purdue.CampusFeed.API.Event;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Sean on 3/8/14.
 */
public class EditEventFragment extends Fragment{
    public Event myEvent;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    public EditText dateSpinner;
    public EditText timeSpinner;
    public static int year;
    public static int month;
    public static int day;
    public static int hour;
    public static String date_time;
    public static int minute;
    private Button doneButton;
    public static String title;
    public static String description;
    public static String location;
    EditText nameText;
    EditText descriptionText;
    EditText locationText;
    
    public static EditEventFragment create(Event event) {
    	EditEventFragment frag = new EditEventFragment();
    	frag.myEvent = event;
    	return frag;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_event, container, false);
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        nameText = (EditText) getActivity().findViewById(R.id.nameText);
        descriptionText = (EditText)getActivity().findViewById(R.id.descriptionText);
        locationText =  (EditText)getActivity().findViewById(R.id.locationText);
        dateSpinner = (EditText) getActivity().findViewById(R.id.dateSpinner);
        timeSpinner = (EditText) getActivity().findViewById(R.id.timeSpinner);
        doneButton = (Button)getActivity().findViewById(R.id.done);

        //set default text field values
        nameText.setText(myEvent.getEventName());
        descriptionText.setText(myEvent.getEventDescription());
        locationText.setText(myEvent.getEventLocation());

        //get current date information from our event
        String[] date = myEvent.getDatetime().split(" ");

        //get current date
        final Calendar cal = Calendar.getInstance();
        year  = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day   = cal.get(Calendar.DAY_OF_MONTH);


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
                HttpClient httpClient = new DefaultHttpClient();

                EditText nameText = (EditText)getActivity().findViewById(R.id.nameText);
                EditText descriptionText = (EditText)getActivity().findViewById(R.id.descriptionText);
                EditText locationText = (EditText)getActivity().findViewById(R.id.locationText);

                //update event data structure
                myEvent.setEventName(nameText.getText().toString());
                myEvent.setEventDescription(descriptionText.getText().toString());
                myEvent.setEventLocation( locationText.getText().toString());
                month=month+1;//account for months indexed at 0
                myEvent.setDatetime( ""+month+"-"+day+"-"+year+" "+hour+":"+minute);
                title=myEvent.getEventName();
                description=myEvent.getEventDescription();
                location=myEvent.getEventLocation();
                date_time=myEvent.getDatetime();

                new ModifyEvent(myEvent).execute("fsdf");
                Toast.makeText(getActivity(),"Event updated to: "+myEvent.getEventLocation()+""+myEvent.getDatetime(),Toast.LENGTH_SHORT).show();
            }
        });
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