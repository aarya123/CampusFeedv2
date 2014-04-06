package com.purdue.CampusFeed.Activities;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import com.purdue.CampusFeed.API.AdvSearchQuery;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import com.purdue.CampusFeed.AsyncTasks.SearchQueryExecutor;
import com.purdue.CampusFeed.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sean on 3/7/14.
 */

public class AdvancedSearchFragment extends Fragment {

    EventArrayAdapter adapter;
    EditText startDateSpinner, endDateSpinner;
    DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    int startYear, startMonth, startDay, endYear, endMonth, endDay;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.advanced_search, container, false);
        /*
        Button searchButton = (Button) view.findViewById(R.id.advSearchButton);
        ListView resultsView = (ListView) view.findViewById(R.id.search_list);
        adapter = new EventArrayAdapter(getActivity(), new ArrayList<Event>());

        resultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Event e = (Event) adapterView.getAdapter().getItem(pos);
                EventPageFragment fragment = new EventPageFragment();
                fragment.setEvent(e);
                //getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                intent.putExtra(getString(R.string.START_FRAGMENT), "EventPageFragment");
                intent.putExtra(getString(R.string.EVENT), e);
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText text = (EditText) getActivity().findViewById(R.id.searchName);
                AdvSearchQuery query = new AdvSearchQuery();
                query.setTitle(text.getText().toString());
                new SearchQueryExecutor(AdvancedSearch_Fragment.this.getActivity(), adapter).execute(query);
            }
        });*/
        return view;
    }
    
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	final Calendar cal = Calendar.getInstance();
    	startYear = cal.get(Calendar.YEAR);
    	startMonth = cal.get(Calendar.MONTH);
    	startDay = cal.get(Calendar.DAY_OF_MONTH);
    	
    	endYear = cal.get(Calendar.YEAR);
    	endMonth = cal.get(Calendar.MONTH);
    	endDay = cal.get(Calendar.DAY_OF_MONTH);
    	
    	
    	startDateSpinner = (EditText) getActivity().findViewById(R.id.searchStartDateSpinner);
    	endDateSpinner = (EditText) getActivity().findViewById(R.id.searchEndDateSpinner);
    	startDatePickerDialog = new DatePickerDialog(getActivity(), startDatePickerListener, startYear, startMonth, startDay);
    	endDatePickerDialog = new DatePickerDialog(getActivity(), endDatePickerListener, endYear, endMonth, endDay);
    	/* Called when dateSpinner is clicked */
        startDateSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show date picker
                startDatePickerDialog.show();
            }
        });
        
        endDateSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show date picker
                endDatePickerDialog.show();
            }
        });
    }
    
    
    /* Called when the user finishes selecting a date from the dialog */
    private DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int selectedYear,
                              int selectedMonth, int selectedDay) {
            startYear = selectedYear;
            startMonth = selectedMonth;
            startDay = selectedMonth;

            // Change spinner's text view to selected date
            startDateSpinner.setText(new StringBuilder().append(startMonth + 1).append("-").append(startDay).append("-").append(startYear).append(" "));
        }
    };
    
    /* Called when the user finishes selecting a date from the dialog */
    private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int selectedYear,
                              int selectedMonth, int selectedDay) {
            endYear = selectedYear;
            endMonth = selectedMonth;
            endDay = selectedMonth;

            // Change spinner's text view to selected date
            endDateSpinner.setText(new StringBuilder().append(endMonth + 1).append("-").append(endDay).append("-").append(endYear).append(" "));
        }
    };
    
}






