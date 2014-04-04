package com.purdue.CampusFeed.Activities;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;

import java.util.ArrayList;

/**
 * User: AnubhawArya
 * Date: 4/4/14
 * Time: 4:58 PM
 */
public class EventListFragment extends ListFragment {

    EventArrayAdapter adapter;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String url = getArguments().getString("url");
        adapter = new EventArrayAdapter(getActivity(), new ArrayList<Event>());
        setListAdapter(adapter);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {

    }
}