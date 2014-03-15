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

import com.purdue.CampusFeed.Activities.CreateEventFragment;
import com.purdue.CampusFeed.Activities.MainActivity;

public class CreateEvent extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... in) {
		HttpClient httpClient = new DefaultHttpClient();
		String title = CreateEventFragment.title;
		String description = CreateEventFragment.description;
		String location = CreateEventFragment.location;
		CreateEventFragment.month = CreateEventFragment.month + 1;
		String time_start = "" + CreateEventFragment.month + "-"
				+ CreateEventFragment.day + "-" + CreateEventFragment.year
				+ " " + CreateEventFragment.hour + ":"
				+ CreateEventFragment.minute;
		String category = "Social"; // set as a default
		try {
			HttpPost request = new HttpPost(
					"http://54.213.17.69:9000/create_event");
			JSONObject requestjson = new JSONObject();
			requestjson.put("desc", description);
			requestjson.put("location", location);
			requestjson.put("category", category);
			requestjson.put("title", title);
			requestjson.put("visibility", 1);
			requestjson.put("date_time", time_start);
			JSONObject auth = new JSONObject();
			auth.put("access_token", MainActivity.SERVER_LONG_TOKEN);
			auth.put("fb_user_id", MainActivity.facebook_userID);
			requestjson.put("auth", auth);
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
