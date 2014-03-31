package com.purdue.CampusFeed.API;

public class Auth {

	public String fb_user_id;
	public String access_token;
	
	public Auth(String fb_user_id, String access_token) {
		this.fb_user_id = fb_user_id;
		this.access_token = access_token;
	}
	
}
