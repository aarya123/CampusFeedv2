package com.purdue.CampusFeed.API;

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
    private String title = null;
    private String desc = null;
    private final int CATEGORY_SIZE = 10;
    private String[] categories = new String[CATEGORY_SIZE];
    private Auth auth;

    public AdvSearchQuery() {
    }

    public AdvSearchQuery(Parcel in) {
        start_date = in.readLong();
        end_date = in.readLong();
        title = in.readString();
        desc = in.readString();
        in.readStringArray(categories);
    }

    public void addCategory(String category) {
        for (int i = 0; i < CATEGORY_SIZE; i++)
            if (categories[i] == null) {
                categories[i] = category;
                return;
            }
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public void setStartDate(long startDate) {
        start_date = startDate;
    }

    public void setEndDate(long endDate) {
        end_date = endDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String[] getCategories() {
        return categories;
    }

    public long getStartDate() {
        return start_date;
    }

    public long getEndDate() {
        return end_date;
    }

    public String getTitle() {
        return title;
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
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeStringArray(categories);
    }

    public static final Parcelable.Creator<AdvSearchQuery> CREATOR = new Parcelable.Creator<AdvSearchQuery>() {
        public AdvSearchQuery createFromParcel(Parcel in) {
            return new AdvSearchQuery(in);
        }

        public AdvSearchQuery[] newArray(int size) {
            return new AdvSearchQuery[size];
        }
    };
}
