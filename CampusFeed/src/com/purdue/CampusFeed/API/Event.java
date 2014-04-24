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

    public final static int PRIVATE = 0, PUBLIC = 1;
    String name, description, location;
    long id, time, status;
    String[] categories;
    int visibility;
    int view_count, is_admin;

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

    public Event(String eventName, String eventDescription, String eventLocation,
                 long datetime, String[] categories, int visibility, long id) {
        this.name = eventName;
        this.description = eventDescription;
        this.location = eventLocation;
        setDatetime(datetime);
        this.categories = categories;
        this.visibility = visibility;
        this.id = id;
    }

    public Event(String eventName, String eventDescription, String eventLocation,
                 long datetime, String[] categories, int visibility) {
        this.name = eventName;
        this.description = eventDescription;
        this.location = eventLocation;
        setDatetime(datetime);
        this.categories = categories;
        this.visibility = visibility;
    }
    
    public Event(String eventName, String eventDescription, String eventLocation,
    			long datetime, String[] categories, int visibility, int view_count, int is_admin) {
    	this(eventName, eventDescription, eventLocation, datetime, categories, visibility);
    	this.view_count = view_count;
    	this.is_admin = is_admin;
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
        String dTime = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss").format(new Date(time * 1000));
        return dTime.substring(0, dTime.length() - 3);
    }

    public void setDatetime(long datetime) {
        this.time = datetime;
    }

    public void setDatetime(String datetime) {
        try {
            this.time = new SimpleDateFormat("M-d-yyyy k:m").parse(datetime).getTime() * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
            this.time = 0;
        }
    }

    public long getDatetimeLong() {
        return time;
    }

    public Long getId() {
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
    
    public int getViewCount() {
    	return view_count;
    }
    
    public boolean isAdmin() {
    	return is_admin != 0;
    }

}
