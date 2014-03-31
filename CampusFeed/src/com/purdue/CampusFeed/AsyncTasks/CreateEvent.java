package com.purdue.CampusFeed.AsyncTasks;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Activities.CreateEventFragment;
import com.purdue.CampusFeed.Activities.MainActivity;

public class CreateEvent extends AsyncTask<String, Void, String> {
	
	private Context context;

	public CreateEvent(Context context) {
		this.context = context;
	}
	@Override
	protected String doInBackground(String... in) {
		String title = CreateEventFragment.title;
		String description = CreateEventFragment.description;
		String location = CreateEventFragment.location;
		CreateEventFragment.month = CreateEventFragment.month + 1;
		String time_start = "" + CreateEventFragment.month + "-"
				+ CreateEventFragment.day + "-" + CreateEventFragment.year
				+ " " + CreateEventFragment.hour + ":"
				+ CreateEventFragment.minute;
		String category = "Social"; // set as a default
		Api.getInstance(context).createEvent(new Event(title, description, location, time_start, new String[]{"Social"}));
		return "done";
	}

	@Override
	protected void onPostExecute(String result) {

		// might want to change "executed" for the returned string passed
		// into onPostExecute() but that is upto you

	}

}
