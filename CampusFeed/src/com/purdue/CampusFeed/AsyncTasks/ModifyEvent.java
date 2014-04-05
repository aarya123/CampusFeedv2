package com.purdue.CampusFeed.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Activities.EditEventFragment;

public class ModifyEvent extends AsyncTask<String, Void, Integer> {
    Event event;
    Context c;

    public ModifyEvent(Event event, Context c) {
        this.event = event;
        this.c = c;
    }

    protected Integer doInBackground(String... in) {
        String title = EditEventFragment.title;
        String description = EditEventFragment.description;
        String location = EditEventFragment.location;
        String time_start = "" + (EditEventFragment.month + 1) + "-" + EditEventFragment.day + "-" +
                EditEventFragment.year + " " + EditEventFragment.hour + ":" + EditEventFragment.minute;
        String[] categories = EditEventFragment.categories; // set as a default
        Api.getInstance(c).updateEvent(new Event(title, description, location, time_start, categories, EditEventFragment.visibility, event.getId()));
        return 0;
    }
}
