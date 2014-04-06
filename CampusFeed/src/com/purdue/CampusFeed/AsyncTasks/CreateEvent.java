package com.purdue.CampusFeed.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.API.Event;

public class CreateEvent extends AsyncTask<Event, Void, Integer> {

    private Context c;

    public CreateEvent(Context context) {
        this.c = context;
    }

    protected Integer doInBackground(Event... events) {
        Event event = events[0];
        if (event.getId() == 0)
            Api.getInstance(c).createEvent(event);
        else
            Api.getInstance(c).updateEvent(event);
        return 0;
    }
}