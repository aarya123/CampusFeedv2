package com.purdue.CampusFeed.API;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
    TODO: probably make parcelable later instead of Serializable
 */
public class Event implements Serializable {

    public final static int PRIVATE = 1, PUBLIC = 0;
    String name, description, location;
    long id, time, status;
    String[] categories;
    int visibility;

    public Event() {
        name = "";
        description = "";
        location = "";
        time = 0;
        id = 0;
        status = 0;
        categories = new String[]{};
        visibility = PUBLIC;
    }

    public Event(String eventName, String eventDescription,
                 String eventLocation, String datetime, String[] categories, int visibility) {
        this.name = eventName;
        this.description = eventDescription;
        this.location = eventLocation;
        setDatetime(datetime);
        this.categories = categories;
        this.visibility = visibility;
    }

    public Event(String eventName, String eventDescription, String eventLocation,
                 String datetime, String[] categories, int visibility, long id) {
        this.name = eventName;
        this.description = eventDescription;
        this.location = eventLocation;
        setDatetime(datetime);
        this.categories = categories;
        this.visibility = visibility;
        this.id = id;
    }

    public static Event JSONToEvent(JSONObject json) {
        return new Event();
    }

    public String getEventName() {
        return name;
    }

    public void setEventName(String eventName) {
        this.name = eventName;
    }

    public String getEventDescription() {
        return description;
    }

    public void setEventDescription(String eventDescription) {
        this.description = eventDescription;
    }

    public String getEventLocation() {
        return location;
    }

    public void setEventLocation(String eventLocation) {
        this.location = eventLocation;
    }

    public String getDatetime() {
        return new SimpleDateFormat("M-d-yyyy k:m").format(new Date(time));
    }

    public void setDatetime(long datetime) {
        this.time = datetime;
    }

    public void setDatetime(String datetime) {
        try {
            this.time = new SimpleDateFormat("M-d-yyyy k:m").parse(datetime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            this.time = 0;
        }
    }

    public long getDatetimeLong() {
        return time;
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

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

}
