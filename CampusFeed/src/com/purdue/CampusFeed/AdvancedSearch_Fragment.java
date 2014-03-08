package com.purdue.CampusFeed;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

/**
 * Created by Sean on 3/7/14.
 */

public class AdvancedSearch_Fragment extends Fragment {
        public static String query;
    ListView resultsView;
        public static ArrayList<Event> results = new ArrayList<Event>();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.advanced_search, container, false);
    }
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //lv = (ListView) getActivity().findViewById(R.id.browse_listview);
        Button searchButton = (Button) getActivity().findViewById(R.id.searchButton);
     resultsView = (ListView)getActivity().findViewById(R.id.search_list);



        resultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                /*String text = (String) adapterView.getItemAtPosition(pos);
                Toast.makeText(getActivity(), text + " selected", Toast.LENGTH_SHORT).show();*/
            Event e = results.get(pos);

            EventPageFragment fragment = new EventPageFragment();
            fragment.setEvent(e);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    });
        
        searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			//	getFragmentManager().beginTransaction().replace(R.id.content_frame, new EventPageFragment()).commit();
				// TODO Auto-generated method stub
                EditText text = (EditText)getActivity().findViewById(R.id.searchName);
                query = text.getText().toString();
                new DOWNLOADER().execute("sdf");
				
				
			}
		});
    }

    class DOWNLOADER extends AsyncTask<String,Void,Integer>
    {
            @Override
        protected Integer doInBackground(String... cat) {
            // clear out results from last time
                results = null;
                results = new ArrayList<Event>();
            HttpClient httpClient = new DefaultHttpClient();

            try {
                HttpPost request = new HttpPost("http://54.213.17.69:9000/search_event");


                JSONObject query = new JSONObject();

                query.put("query",AdvancedSearch_Fragment.query);
                StringEntity params =new StringEntity(query.toString());
                request.addHeader("content-type", "application/json");
                request.setEntity(params);
                HttpResponse response1= httpClient.execute(request);
                // get response
                HttpEntity res = response1.getEntity();

                JSONArray r = new JSONArray( EntityUtils.toString(res));
                if(r.length()==0)
                {
                    return 0;
                }
                for(int i=0;i<r.length();i++)
                {
                    Event e = new Event();
                    JSONObject current = r.getJSONObject(i);
                    e.eventName = current.getString("name");
                    e.datetime=current.getString("time");
                    e.eventDescription=current.getString("description");
                    e.eventLocation=current.getString("location");
                    results.add(e);
                }
                return 1;

            }catch (Exception ex) {
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

            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            if(result==0)
            {
                Event e = new Event();
                e.eventName="No Results Found";
                results.add(e);

            }
            RowGenerator_ArrayAdapter rs = new RowGenerator_ArrayAdapter(getActivity(),results);
            resultsView.setAdapter(rs);
            rs.notifyDataSetChanged();

        }

    }
}

