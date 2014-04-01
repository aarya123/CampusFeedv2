package com.purdue.CampusFeed.Activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.AsyncTasks.GetEventsByTag;
import com.purdue.CampusFeed.API.Event;

import java.util.ArrayList;

/**
 * Created by Sean on 3/7/14.
 */

public class BrowseFragment extends Fragment {

    EventArrayAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*View view = inflater.inflate(R.layout.browse_fragment, container, false);
        LinearLayout browseTagList = (LinearLayout) view.findViewById(R.id.browseTagList);
        for (int i = 0; i < 10; i++)
            browseTagList.addView(inflater.inflate(R.layout.browse_tag, browseTagList));*/
        ListView listView = new ListView(getActivity());
        adapter = new EventArrayAdapter(getActivity(), new ArrayList<Event>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int pos, long id) {
                Event e = adapter.getItem(pos);
                EventPageFragment fragment = new EventPageFragment();
                fragment.setEvent(e);
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment).commit();
            }
        });
        new GetEventsByTag(adapter).execute("ds");
        return listView;
        //return view;
    }

}