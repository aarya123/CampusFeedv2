package com.purdue.CampusFeed.API;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
    TODO: probably make parcelable later instead of Serializable
 */
public class Event implements Parcelable {

    public final static int PRIVATE = 0, PUBLIC = 1;
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return null;
        }
    };
    String name, description, location;
    long id, time, status;
    String[] categories;
    int visibility;
    int view_count, is_admin;
    Creator creator = new Creator("", "");

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
                 long datetime, String[] categories, int visibility, int view_count, int is_admin, Creator creator) {
        this(eventName, eventDescription, eventLocation, datetime, categories, visibility);
        this.view_count = view_count;
        this.is_admin = is_admin;
        this.creator = creator;
    }

    public Event(Parcel in) {
        //TODO implement parcelable constructor
        /*
        public final static int PRIVATE = 0, PUBLIC = 1;
        String name, description, location;
        long id, time, status;
        String[] categories;
        int visibility;
        int view_count, is_admin;
        Creator creator = new Creator("", "");
        */
        name = in.readString();
        description = in.readString();
        location = in.readString();
        id = in.readLong();
        time = in.readLong();
        status = in.readLong();
        categories = new String[in.readInt()];
        in.readStringArray(categories);
        visibility = in.readInt();
        view_count = in.readInt();
        is_admin = in.readInt();
        creator.first_name = in.readString();
        creator.last_name = in.readString();
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

    public void setDatetime(String datetime) {
        try {
            this.time = new SimpleDateFormat("M-d-yyyy k:m").parse(datetime).getTime() * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
            this.time = 0;
        }
    }

    public void setDatetime(long datetime) {
        this.time = datetime;
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

    public boolean hasTag(String tag) {
        for (String str : categories)
            if (str.equals(tag))
                return true;
        return false;
    }

    public Creator getCreator() {
        return creator;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.location);
        dest.writeLong(this.id);
        dest.writeLong(this.time);
        dest.writeLong(this.status);
        dest.writeInt(this.categories.length);
        if (this.categories.length > 0) {
            dest.writeStringArray(this.categories);
        }
        dest.writeInt(this.visibility);
        dest.writeInt(this.view_count);
        dest.writeInt(this.is_admin);
        dest.writeString(this.creator.first_name);
        dest.writeString(this.creator.last_name);
    }

    public static class Creator {
        private String first_name;
        private String last_name;

        public Creator(String first_name, String last_name) {
            this.first_name = first_name;
            this.last_name = last_name;
        }

        public String getFirstName() {
            return first_name;
        }

        public String getLastName() {
            return last_name;
        }

        public String getName() {
            return first_name + " " + last_name;
        }
    }

}
