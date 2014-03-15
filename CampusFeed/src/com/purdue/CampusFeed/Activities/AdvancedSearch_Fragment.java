package com.purdue.CampusFeed.Activities;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import com.purdue.CampusFeed.AsyncTasks.SearchEvents;

import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.API.Api.Callback;
import com.purdue.CampusFeed.API.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sean on 3/7/14.
 */

public class AdvancedSearch_Fragment extends Fragment {

    EventArrayAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.advanced_search, container, false);
        Api test = new Api(getActivity());
        test.asyncSearchEvent("", new Callback<List<com.purdue.CampusFeed.API.Event>>() {

            @Override
            public void call(List<com.purdue.CampusFeed.API.Event> data) {
                if (data != null) {
                    Log.i("test", data.toString());
                } else {
                    Log.i("test", "null");
                }
            }

        });
        try {
            test.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Button searchButton = (Button) view.findViewById(R.id.searchButton);
        ListView resultsView = (ListView) view.findViewById(R.id.search_list);
        adapter = new EventArrayAdapter(getActivity(), new ArrayList<Event>());

        resultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Event e = (Event) adapterView.getAdapter().getItem(pos);
                EventPageFragment fragment = new EventPageFragment();
                fragment.setEvent(e);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText text = (EditText) getActivity().findViewById(R.id.searchName);
                String query = text.getText().toString();
                new SearchEvents(adapter).execute(query);
            }
        });
        return view;
    }
}

