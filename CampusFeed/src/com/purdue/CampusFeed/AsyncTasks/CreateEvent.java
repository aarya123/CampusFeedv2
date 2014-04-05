package com.purdue.CampusFeed.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.API.Event;

public class CreateEvent extends AsyncTask<Event, Void, String> {

    private Context context;

    public CreateEvent(Context context) {
        this.context = context;
    }

    protected String doInBackground(Event... event) {
        Api.getInstance(context).createEvent(event[0]);
        return "";
    }
}
