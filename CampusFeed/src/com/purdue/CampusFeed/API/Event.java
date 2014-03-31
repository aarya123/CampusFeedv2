package com.purdue.CampusFeed.API;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.purdue.CampusFeed.Utils.Utils;

public class Event {

    String eventName, eventDescription, eventLocation, datetime;
    long id;
    String[] categories;

    public Event() {
        eventName = "";
        eventDescription = "";
        eventLocation = "";
        datetime = "";
        id = 0;
        categories = new String[]{};
    }

    public Event(String eventName, String eventDescription,
                 String eventLocation, String datetime, String[] categories) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.datetime = datetime;
        this.categories = categories;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public static Event JSONToEvent(JSONObject json) {
        try {
            String[] categories = new String[json.getJSONArray("categories").length()];
            for (int i = 0; i < json.getJSONArray("categories").length(); i++)
                categories[i] = json.getJSONArray("categories").getString(i);
            return new Event(json.getString("title"),
                    json.getString("desc"),
                    json.getString("location"),
                    json.getString("date_time"),
                    categories);

        } catch (JSONException e) {
            Log.e(Utils.TAG, e.getMessage());
            return new Event("", "", "", "", new String[]{""});

        }

    }
}
