package com.purdue.CampusFeed.API;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User: AnubhawArya
 * Date: 4/4/14
 * Time: 5:50 PM
 */

public class AdvSearchQuery implements Parcelable {
    private long start_date = 0;
    private long end_date = Long.MAX_VALUE;
    private String name = null;
    private String desc = null;
    private	ArrayList<String> tags = null;
    private Auth auth;

    public AdvSearchQuery() {
    }

    public AdvSearchQuery(Parcel in) {
        start_date = in.readLong();
        end_date = in.readLong();
        name = in.readString();
        desc = in.readString();
        tags = new ArrayList<String>();
        in.readStringList(tags);
    }

    public void settags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setStartDate(long startDate) {
        start_date = startDate;
    }

    public void setEndDate(long endDate) {
        end_date = endDate;
    }

    public void setTitle(String title) {
        this.name = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList<String> gettags() {
        return tags;
    }

    public long getStartDate() {
        return start_date;
    }

    public long getEndDate() {
        return end_date;
    }

    public String getTitle() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int describeContents() {
        return 0;
    }
    
    public void setAuth(Auth auth) {
    	this.auth = auth;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(start_date);
        dest.writeLong(end_date);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeStringList(tags);
    }

    public static final Parcelable.Creator<AdvSearchQuery> CREATOR = new Parcelable.Creator<AdvSearchQuery>() {
        public AdvSearchQuery createFromParcel(Parcel in) {
            return new AdvSearchQuery(in);
        }

        public AdvSearchQuery[] newArray(int size) {
            return new AdvSearchQuery[size];
        }
    };

	public void addCategory(String category) {
		if(tags == null) {
			tags = new ArrayList<String>();
		}
		tags.add(category);
	}
}
