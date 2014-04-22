package com.purdue.CampusFeed.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Adapters.Top5Adapter;
import com.purdue.CampusFeed.AsyncTasks.Top5Events;
import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.Utils.Utils;

import java.util.ArrayList;

/**
 * Created by Sean on 3/7/14.
 */
public class HomepageFragment extends Fragment {

    Top5Adapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ListView listView = new ListView(getActivity());
        adapter = new Top5Adapter(getActivity(), new ArrayList<Event>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                try {
                    Event e = adapter.getItem(pos);
                    Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                    intent.putExtra(getString(R.string.START_FRAGMENT), "EventPageFragment");
                    intent.putExtra(getString(R.string.EVENT), e);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.d(Utils.TAG, e.getMessage());
                }
            }
        });
        new Top5Events(getActivity(), adapter).execute(Utils.categories);
        return listView;
    }

}
