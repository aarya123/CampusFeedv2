package com.purdue.CampusFeed.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import com.purdue.CampusFeed.API.AdvSearchQuery;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;

import java.util.List;

/**
 * User: AnubhawArya
 * Date: 4/4/14
 * Time: 5:24 PM
 */
public class SearchQueryExecutor extends AsyncTask<AdvSearchQuery, Integer, List<Event>> {

    Context c;
    EventArrayAdapter adapter;

    public SearchQueryExecutor(Context c, EventArrayAdapter adapter) {
        this.c = c;
        this.adapter = adapter;
    }

    protected List<Event> doInBackground(AdvSearchQuery... params) {
        return Api.getInstance(c).advSearchEvent(params[0]);
    }

    protected void onPostExecute(List<Event> list) {
        super.onPostExecute(list);
        if (list != null)
            adapter.addAll(list);
        adapter.notifyDataSetChanged();
    }
}
