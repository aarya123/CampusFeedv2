package com.purdue.CampusFeed.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.purdue.CampusFeed.API.AdvSearchQuery;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.Utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sean on 3/7/14.
 */

public class AdvancedSearchFragment extends Fragment implements OnClickListener {

    EventArrayAdapter adapter;
    EditText startDateSpinner, endDateSpinner;
    DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    int startYear, startMonth, startDay, endYear, endMonth, endDay;
    /* Called when the user finishes selecting a date from the dialog */
    private DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int selectedYear,
                              int selectedMonth, int selectedDay) {
            startYear = selectedYear;
            startMonth = selectedMonth;
            startDay = selectedDay;

            // Change spinner's text view to selected date
            startDateSpinner.setText(new StringBuilder().append(startMonth + 1).append("-").append(startDay).append("-").append(startYear).append(" "));
        }
    };
    MultiAutoCompleteTextView searchTag;
    /* Called when the user finishes selecting a date from the dialog */
    private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int selectedYear,
                              int selectedMonth, int selectedDay) {
            endYear = selectedYear;
            endMonth = selectedMonth;
            endDay = selectedDay;

            // Change spinner's text view to selected date
            endDateSpinner.setText(new StringBuilder().append(endMonth + 1).append("-").append(endDay).append("-").append(endYear).append(" "));
        }
    };

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
        searchTag = (MultiAutoCompleteTextView) getActivity().findViewById(R.id.searchTagText);
        searchTag.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, Utils.categories));
        searchTag.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
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

        Button searchButton = (Button) getActivity().findViewById(R.id.advSearchButton);
        searchButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        AdvSearchQuery query = new AdvSearchQuery();
        EditText searchName = (EditText) getActivity().findViewById(R.id.searchNameText);
        EditText searchDescription = (EditText) getActivity().findViewById(R.id.searchDescriptionText);
        EditText searchStartDate = (EditText) getActivity().findViewById(R.id.searchStartDateSpinner);
        EditText searchEndDate = (EditText) getActivity().findViewById(R.id.searchEndDateSpinner);

        String name = searchName.getText().toString();
        String description = searchDescription.getText().toString();
        ArrayList<String> tag = new ArrayList<String>();
        //Collections.addAll(tag, searchTag.getText().toString().split(" "));
        String[] tags = searchTag.getText().toString().split(" ");
        String startTime = searchStartDate.getText().toString();
        String endTime = searchEndDate.getText().toString();

        for (int i = 0; i < tags.length; i++) {
            if (!tags[i].equals("")) {
                tag.add(tags[i]);
            }
        }

        if (!name.isEmpty()) {
            query.setTitle(name);
        }
        if (!description.isEmpty()) {
            query.setDesc(description);
        }
        if (tag.size() != 0) {
            query.settags(tag);
        }
        if (!startTime.isEmpty()) {
            startTime = startTime.trim();
            Log.e("tag", "START TIME =======" + startTime + "========");
            if (startTime.charAt(1) == '-') {
                startTime = "0" + startTime;
            }
            if (startTime.charAt(4) == '-') {
                // 01-1-2014 --> 01-01-2014
                startTime = startTime.substring(0, 3) + "0" + startTime.substring(3);
            }
            Log.e("tag", "START TIME =======" + startTime + "========");

            Date date;
            DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
            try {
                date = (Date) formatter.parse(startTime);
                query.setStartDate(date.getTime() / 1000);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (!endTime.isEmpty()) {
            endTime = endTime.trim();
            if (endTime.charAt(1) == '-') {
                endTime = "0" + endTime;
            }
            if (endTime.charAt(4) == '-') {
                // 01-1-2014 --> 01-01-2014
                endTime = endTime.substring(0, 3) + "0" + endTime.substring(3);
            }

            Date date;
            DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
            try {
                date = (Date) formatter.parse(endTime);
                query.setEndDate(date.getTime() / 1000);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        EventListFragment listFragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putParcelable("query", query);
        listFragment.setArguments(args);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, listFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}






