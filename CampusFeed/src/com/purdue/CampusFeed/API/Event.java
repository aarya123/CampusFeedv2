package com.purdue.CampusFeed.API;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.purdue.CampusFeed.Utils.Utils;

public class Event {

    String eventName, eventDescription, eventLocation, datetime, id;

    public Event(String eventName, String eventDescription,
                 String eventLocation, String datetime, String id) {
        super();
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.datetime = datetime;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public String toString() {
        return GsonHelper.createCampusFeedGson().toJson(this);
    }

    public static Event JSONToEvent(JSONObject json) {
        try {
            return new Event(json.getString("title"),
                    json.getString("desc"),
                    json.getString("location"),
                    json.getString("date_time"),
                    json.getString("id"));
        } catch (JSONException e) {
            Log.e(Utils.TAG, e.getMessage());
            return new Event("", "", "", "", "");
        }
    }
}
