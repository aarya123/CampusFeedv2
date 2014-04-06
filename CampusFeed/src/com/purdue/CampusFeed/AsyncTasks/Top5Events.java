package com.purdue.CampusFeed.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import com.purdue.CampusFeed.Utils.Utils;

import java.util.ArrayList;

/**
 * User: AnubhawArya
 * Date: 4/5/14
 * Time: 11:41 PM
 */
public class Top5Events extends AsyncTask<String[], Integer, ArrayList<Event>> {
    Context c;
    EventArrayAdapter adapter;

    public Top5Events(Context c, EventArrayAdapter adapter) {
        this.c = c;
        this.adapter = adapter;
    }

    protected ArrayList<Event> doInBackground(String[]... params) {
        ArrayList<Event> temp = new ArrayList<Event>();
        Api api = Api.getInstance(c);
        Log.d(Utils.TAG, "Size " + params.length);
        for (int i = 0; i < params.length; i++) {
            for (int j = 0; i < params[i].length; j++) {
                Log.d(Utils.TAG, params[i][j]);
                temp.addAll(api.top5(params[i][j]));
            }
        }
        return temp;
    }

    protected void onPostExecute(ArrayList<Event> events) {
        super.onPostExecute(events);
        adapter.addAll(events);
    }
}
