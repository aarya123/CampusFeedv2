package com.purdue.CampusFeed.AsyncTasks;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.purdue.CampusFeed.Model.Event;

public class GetEventsByTag extends AsyncTask<String, Void, Integer> {
	
public GetEventsByTag(){}

		protected Integer doInBackground(String... cat) {

			HttpClient httpClient = new DefaultHttpClient();

			try {
				HttpPost request = new HttpPost("http://54.213.17.69:9000/all");

				HttpResponse response1 = httpClient.execute(request);
				// get response
				HttpEntity res = response1.getEntity();

				JSONArray r = new JSONArray(EntityUtils.toString(res));
				Log.d("MAYANK123", r.toString());

				for (int i = 0; i < r.length(); i++) {
					JSONObject current = r.getJSONObject(i);
					Event e = Event.JSONToEvent(current);
					events.add(e);
				}
				// handle response here...
			} catch (Exception ex) {
				// handle exception here
				Log.d("MAYANKIN BROWSE", ex.toString());
				ex.printStackTrace();
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
			return 1;

		}

		protected void onPostExecute(Integer result) {

			adapter.notifyDataSetChanged();

		}

	}


