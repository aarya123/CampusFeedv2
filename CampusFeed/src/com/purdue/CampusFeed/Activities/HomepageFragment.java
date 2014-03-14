package com.purdue.CampusFeed.Activities;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.Adapters.RowGenerator_ArrayAdapter;
import com.purdue.CampusFeed.AsyncTasks.TopFiveEvents;
import com.purdue.CampusFeed.Model.Event;
import com.purdue.CampusFeed.R.drawable;
import com.purdue.CampusFeed.R.id;
import com.purdue.CampusFeed.R.layout;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sean on 3/7/14.
 */
public class HomepageFragment extends Fragment {
	private ListView lv1;
	RowGenerator_ArrayAdapter adapter1;
	RowGenerator_ArrayAdapter adapter2;
	RowGenerator_ArrayAdapter adapter3;
	private ListView lv2;
	private ListView lv3;

	public static ArrayList<Event> firstcat = new ArrayList<Event>();
	public static ArrayList<Event> secondcat = new ArrayList<Event>();
	public static ArrayList<Event> thirdcat = new ArrayList<Event>();

	private static final String[] categories = new String[] { "Social",
			"Cultural", "Education" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.homepage_layout, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final ListView list = (ListView) getActivity().findViewById(
				R.id.home_list);
		
		new TopFiveEvents().execute(categories);

	}
}
