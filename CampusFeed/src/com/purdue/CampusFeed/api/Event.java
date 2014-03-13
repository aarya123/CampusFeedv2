package com.purdue.CampusFeed.api;

import java.util.Date;

public class Event {

	public int id;
	public String location;
	public Date time;
	public String description;
	public String status;
	public String category;
	
	@Override
	public String toString() {
		return GsonHelper.createCampusFeedGson().toJson(this);
	}
	
}
