package com.purdue.CampusFeed.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import com.purdue.CampusFeed.API.Event;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class TopFiveEvents extends AsyncTask<String, Void, String> {
    EventArrayAdapter adapter;

    public TopFiveEvents(EventArrayAdapter adapter) {
        this.adapter = adapter;
    }

    protected String doInBackground(String... categories) {
        try {
            for (String category : categories) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(
                        "http://54.213.17.69:9000/top5");
                JSONObject requestjson = new JSONObject();
                requestjson.put("category", category);
                Log.d("campus", requestjson.toString());
                StringEntity params = new StringEntity(
                        requestjson.toString());
                request.addHeader("content-type", "application/json");
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);
                // get response
                HttpEntity res = response.getEntity();
                JSONArray responseArray = new JSONArray(
                        EntityUtils.toString(res));
                for (int i = 0; i < responseArray.length(); ++i) {
                    JSONObject current = responseArray.getJSONObject(i);
                    Event e = Event.JSONToEvent(current);
                    adapter.add(e);
                }
            }
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(String result) {
        if (result == null)
            return;
        Log.i("result", result.toString());

    }

}