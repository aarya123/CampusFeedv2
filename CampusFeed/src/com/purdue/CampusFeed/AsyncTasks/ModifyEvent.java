package com.purdue.CampusFeed.AsyncTasks;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.purdue.CampusFeed.Activities.EditEventFragment;
import com.purdue.CampusFeed.Model.Event;

public class ModifyEvent extends AsyncTask<String, Void, String> {
	Event event;

	public ModifyEvent(Event event) {
		this.event = event;
	}

	@Override
	protected String doInBackground(String... in) {
		HttpClient httpClient = new DefaultHttpClient();
		String title = EditEventFragment.title;
		String description = EditEventFragment.description;
		String location = EditEventFragment.location;
		EditEventFragment.month = EditEventFragment.month + 1;
		String category = "Social"; // set as a default
		try {
			HttpPost request = new HttpPost(
					"http://54.213.17.69:9000/update_event");
			JSONObject requestjson = new JSONObject();
			requestjson.put("desc", description);
			requestjson.put("location", location);
			requestjson.put("category", category);
			requestjson.put("title", title);
			requestjson.put("id", event.getId());

			requestjson.put("date_time", event.getDatetime());

			Log.d("campus", requestjson.toString());

			StringEntity params = new StringEntity(requestjson.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			// get response
			HttpEntity res = response.getEntity();
			Log.d("MAYANK", EntityUtils.toString(res) + "response");
			// handle response here...
		} catch (Exception ex) {
			// handle exception here
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return "done";
	}

	@Override
	protected void onPostExecute(String result) {

		// might want to change "executed" for the returned string passed
		// into onPostExecute() but that is upto you

	}

}
