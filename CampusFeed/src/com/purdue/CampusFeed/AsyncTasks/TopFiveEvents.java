package com.purdue.CampusFeed.AsyncTasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.Activities.EventPageFragment;
import com.purdue.CampusFeed.Model.Event;

public class TopFiveEvents extends AsyncTask<String, Void, List<Object>> {

		@Override
		protected List<Object> doInBackground(String... categories) {
			try {
				List<Object> top5 = new ArrayList<Object>();
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
					top5.add(category);
					for (int i = 0; i < responseArray.length(); ++i) {
						JSONObject current = responseArray.getJSONObject(i);
						Event e = Event.JSONToEvent(current);
						top5.add(e);
					}
				}
				return top5;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		protected void onPostExecute(final List<Object> result) {
			if (result == null)
				return;
			Log.i("result", result.toString());
			list.setAdapter(new BaseAdapter() {

				private static final int HEADER = 0;
				private static final int EVENT = 1;

				@Override
				public int getCount() {
					return result.size();
				}

				@Override
				public Object getItem(int pos) {
					return result.get(pos);
				}

				@Override
				public long getItemId(int pos) {
					return 0;
				}

				@Override
				public int getItemViewType(int pos) {
					if (result.get(pos) instanceof String) {
						return HEADER;
					} else if (result.get(pos) instanceof Event) {
						return EVENT;
					} else {
						throw new UnsupportedOperationException();
					}
				}

				@Override
				public int getViewTypeCount() {
					return 2;
				}

				@Override
				public View getView(int pos, View convertView,
						ViewGroup container) {
					if (getItemViewType(pos) == HEADER) {
						TextView view = (TextView) convertView;
						if (view == null) {
							view = new TextView(getActivity());
						}
						view.setText((String) result.get(pos));
						return view;
					} else if (getItemViewType(pos) == EVENT) {
						LayoutInflater inflater = (LayoutInflater) getActivity()
								.getSystemService(
										Context.LAYOUT_INFLATER_SERVICE);
						View rowView = convertView;
						if (rowView == null) {
							rowView = inflater.inflate(
									R.layout.browseevents_rowlayout,
									container, false);
						}
						try {
							// set the required text and image
							ImageView eventImage = (ImageView) rowView
									.findViewById(R.id.event_image);
							TextView eventName = (TextView) rowView
									.findViewById(R.id.event_name);
							TextView eventTime = (TextView) rowView
									.findViewById(R.id.event_time);
							TextView eventDescription = (TextView) rowView
									.findViewById(R.id.event_location);

							Event event = (Event) result.get(pos);
							eventName.setText(event.getEventName());
							// eventTime.setText(values[position]);
							eventDescription
									.setText(event.getEventDescription());
							eventImage
									.setImageResource(R.drawable.purdue_symbol);
						} catch (Exception e) {
							e.printStackTrace();
							Log.d("mayankerror", e.toString());
						}
						return rowView;
					} else {
						throw new UnsupportedOperationException();
					}
				}

			});
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView,
						View view, int pos, long id) {
					try {
						Event e = (Event) result.get(pos);
						EventPageFragment fragment = new EventPageFragment();
						fragment.setEvent(e);
						getFragmentManager().beginTransaction()
								.replace(R.id.content_frame, fragment)
								.commit();
					} catch (ClassCastException e) {

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
		}

	}