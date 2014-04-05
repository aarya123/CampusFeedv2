package com.purdue.CampusFeed.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.API.Event;

public class CreateEvent extends AsyncTask<Event, Void, Integer> {

    private Context context;

    public CreateEvent(Context context) {
        this.context = context;
    }

    protected Integer doInBackground(Event... event) {
        Api.getInstance(context).createEvent(event[0]);
        return 0;
    }
}
