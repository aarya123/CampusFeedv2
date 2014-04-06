package com.purdue.CampusFeed.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.purdue.CampusFeed.API.AdvSearchQuery;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import com.purdue.CampusFeed.AsyncTasks.SearchQueryExecutor;
import com.purdue.CampusFeed.R;

import java.util.ArrayList;

/**
 * User: AnubhawArya
 * Date: 4/4/14
 * Time: 4:58 PM
 */
public class EventListFragment extends ListFragment {

    EventArrayAdapter adapter;

    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
        AdvSearchQuery query = getArguments().getParcelable("query");
        Log.i("ATTACH", query.getTitle());
        adapter = new EventArrayAdapter(activity, new ArrayList<Event>());
        setListAdapter(adapter);
        new SearchQueryExecutor(activity, adapter).execute(query);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        Event e = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
        intent.putExtra(getString(R.string.START_FRAGMENT), "EventPageFragment");
        intent.putExtra(getString(R.string.EVENT), e);
        startActivity(intent);
    }

}