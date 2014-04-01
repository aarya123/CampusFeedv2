package com.purdue.CampusFeed.Activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import com.purdue.CampusFeed.AsyncTasks.TopFiveEvents;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.R;

import java.util.ArrayList;

/**
 * Created by Sean on 3/7/14.
 */
public class HomepageFragment extends Fragment {

    private static final String[] categories = new String[]{"Social",
            "Cultural", "Education"};
    EventArrayAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    
        ListView listView = new ListView(getActivity());
        adapter = new EventArrayAdapter(getActivity(), new ArrayList<Event>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int pos, long id) {
                try {
                    Event e = adapter.getItem(pos);
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
        new TopFiveEvents(adapter).execute(categories);
        return listView;
    }
    
}
