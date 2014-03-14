package com.purdue.CampusFeed.Activities;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.Adapters.RowGenerator_ArrayAdapter;
import com.purdue.CampusFeed.AsyncTasks.GetEventsByTag;
import com.purdue.CampusFeed.Model.Event;
import com.purdue.CampusFeed.R.id;
import com.purdue.CampusFeed.R.layout;

import java.util.ArrayList;

/**
 * Created by Sean on 3/7/14.
 */

public class BrowseFragment extends Fragment {
	private ListView lv;

	RowGenerator_ArrayAdapter adapter;
	static ArrayList<Event> events = new ArrayList<Event>();

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.browse_layout, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// move list view initialization here
		lv = (ListView) getActivity().findViewById(R.id.browse_listview);
		adapter = new RowGenerator_ArrayAdapter(getActivity(), events);

		new GetEventsByTag().execute("ds");

		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int pos, long id) {
				/*
				 * String text = (String) adapterView.getItemAtPosition(pos);
				 * Toast.makeText(getActivity(), text + " selected",
				 * Toast.LENGTH_SHORT).show();
				 */
				Event e = events.get(pos);
				EventPageFragment fragment = new EventPageFragment();
				fragment.setEvent(e);
				getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, fragment).commit();
			}
		});
	}


}