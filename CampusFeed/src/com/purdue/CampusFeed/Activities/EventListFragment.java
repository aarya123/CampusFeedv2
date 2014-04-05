package com.purdue.CampusFeed.Activities;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import com.purdue.CampusFeed.API.AdvSearchQuery;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import com.purdue.CampusFeed.AsyncTasks.SearchQueryExecutor;

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
        AdvSearchQuery query = getArguments().getParcelable("query");
        adapter = new EventArrayAdapter(getActivity(), new ArrayList<Event>());
        setListAdapter(adapter);
        new SearchQueryExecutor(getActivity(), adapter).execute(query);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {

    }

}