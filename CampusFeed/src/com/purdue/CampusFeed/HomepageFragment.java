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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.homepage_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        lv1 = (ListView) getActivity().findViewById(R.id.listView);
        lv2 = (ListView) getActivity().findViewById(R.id.listView2);
        lv3 = (ListView) getActivity().findViewById(R.id.listView3);
       adapter1= new RowGenerator_ArrayAdapter(getActivity(), HomepageFragment.firstcat);
        adapter2 = new RowGenerator_ArrayAdapter(getActivity(), secondcat);
        adapter3 = new RowGenerator_ArrayAdapter(getActivity(), thirdcat);
        lv1.setAdapter(adapter1);
        lv2.setAdapter(adapter2);
        lv3.setAdapter(adapter3);

        lv1.setOnItemClickListener(itemClickedListener);
        lv2.setOnItemClickListener(itemClickedListener);
        DOWNLOADER d1 = new DOWNLOADER();
        DOWNLOADER d2 = new DOWNLOADER();
        DOWNLOADER d3 = new DOWNLOADER();
        d1.execute("Social","1");
        d2.execute("Cultural","2");
        d3.execute("Sports","3");

    }
    ListView.OnItemClickListener itemClickedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            Event e = firstcat.get(pos);

            EventPageFragment fragment = new EventPageFragment();
            fragment.setEvent(e);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    };


    class DOWNLOADER extends AsyncTask<String,Void,Integer>
    {

        protected Integer doInBackground(String... cat) {

            HttpClient httpClient = new DefaultHttpClient();
            Integer which = Integer.parseInt(cat[1]);
            try {
                HttpPost request = new HttpPost("http://54.213.17.69:9000/top3cat");


                JSONObject getter = new JSONObject();
                getter.put("category",cat[0]);

                Log.d("campus",getter.toString());

                StringEntity params =new StringEntity(getter.toString());
                request.addHeader("content-type", "application/json");
                request.setEntity(params);
                HttpResponse response1= httpClient.execute(request);
                // get response
                HttpEntity res = response1.getEntity();

                JSONArray r = new JSONArray( EntityUtils.toString(res));
                Log.d("MAYANK123",r.toString());

                for(int i=0;i<r.length();i++)
                {
                    if(which==1){
                        Event e = new Event();
                        JSONObject current = r.getJSONObject(i);
                        e.eventName = current.getString("title");
                        e.datetime=current.getString("date_time");
                        e.eventDescription=current.getString("desc");
                        e.eventLocation=current.getString("location");
                        HomepageFragment.firstcat.add(e);


                    }
                    else
                    if(which==2)
                    {
                        Event e = new Event();
                        JSONObject current = r.getJSONObject(i);
                        e.eventName = current.getString("title");
                        e.datetime=current.getString("date_time");
                        e.eventDescription=current.getString("desc");
                        e.eventLocation=current.getString("location");
                        HomepageFragment.secondcat.add(e);

                    }
                    else
                    if(which==3)
                    {
                        Event e = new Event();
                        JSONObject current = r.getJSONObject(i);
                        e.eventName = current.getString("title");
                        e.datetime=current.getString("date_time");
                        e.eventDescription=current.getString("desc");
                        e.eventLocation=current.getString("location");
                        HomepageFragment.thirdcat.add(e);
                    }

                }
                // handle response here...
            }catch (Exception ex) {
                // handle exception here
                Log.d("MAYANK", ex.toString());
                ex.printStackTrace();
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return which;

        }


        protected void onPostExecute(Integer result) {

            if(result==1)
            {

                adapter1.notifyDataSetChanged();

            }
            else
            if(result==2)
            {

                adapter2.notifyDataSetChanged();


            }
            else
            if(result==3)
            {

                adapter3.notifyDataSetChanged();

            }

        }


    }

}

