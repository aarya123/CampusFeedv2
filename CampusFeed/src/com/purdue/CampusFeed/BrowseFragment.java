package com.purdue.CampusFeed;

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

import java.util.ArrayList;

/**
 * Created by Sean on 3/7/14.
 */

public class BrowseFragment extends Fragment {
    private ListView lv;



    RowGenerator_ArrayAdapter adapter;
   static  ArrayList<Event> events = new ArrayList<Event>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.browse_layout, container, false);
    }
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //move list view initialization here
        lv = (ListView) getActivity().findViewById(R.id.browse_listview);
         adapter = new RowGenerator_ArrayAdapter(getActivity(), events);

        new  DOWNLOADER().execute("ds");



        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                /*String text = (String) adapterView.getItemAtPosition(pos);
                Toast.makeText(getActivity(), text + " selected", Toast.LENGTH_SHORT).show();*/
                Event e = events.get(pos);

                getFragmentManager().beginTransaction().replace(R.id.content_frame, new EventPageFragment(e)).commit();
            }
        });
    }

    class DOWNLOADER extends AsyncTask<String,Void,Integer>
    {

        protected Integer doInBackground(String... cat) {

            HttpClient httpClient = new DefaultHttpClient();

            try {
                HttpPost request = new HttpPost("http://54.213.17.69:9000/all");



                HttpResponse response1= httpClient.execute(request);
                // get response
                HttpEntity res = response1.getEntity();

                JSONArray r = new JSONArray( EntityUtils.toString(res));
                Log.d("MAYANK123",r.toString());

                for(int i=0;i<r.length();i++)
                {


                        Event e = new Event();
                        JSONObject current = r.getJSONObject(i);
                        e.eventName = current.getString("title");
                        e.id=current.getString("id");
                        e.datetime=current.getString("date_time");
                        e.eventDescription=current.getString("desc");
                        e.eventLocation=current.getString("location");
                        events.add(e);


                }
                // handle response here...
            }catch (Exception ex) {
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

}