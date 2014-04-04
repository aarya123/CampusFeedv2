package com.purdue.CampusFeed.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchEvents extends AsyncTask<String, Void, Integer> {

    EventArrayAdapter adapter;
    ArrayList<Event> eventArray;

    public SearchEvents(EventArrayAdapter adapter) {
        this.adapter = adapter;
        eventArray = new ArrayList<Event>();
    }

    protected Integer doInBackground(String... cat) {
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost request = new HttpPost("http://54.213.17.69:9000/search_event");
            JSONObject query = new JSONObject();
            query.put("query", cat[0]);
            StringEntity params = new StringEntity(query.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response1 = httpClient.execute(request);
            // get response
            HttpEntity res = response1.getEntity();

            JSONArray r = new JSONArray(EntityUtils.toString(res));
            if (r.length() == 0) {
                return 0;
            }
            for (int i = 0; i < r.length(); i++) {
                JSONObject current = r.getJSONObject(i);
                Event e = Event.JSONToEvent(current);
                //adapter.add(e);
                eventArray.add(e);
            }
            return 1;

        } catch (Exception ex) {
            // handle exception here
            Log.d("MAYANKIN BROWSE", ex.toString());
            ex.printStackTrace();
            return 0;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == 0) {
            Event e = Event.JSONToEvent(new JSONObject());
            e.setEventName("No Results Found");
            adapter.add(e);

        }
        adapter.addAll(eventArray);
        adapter.notifyDataSetChanged();
    }
}
