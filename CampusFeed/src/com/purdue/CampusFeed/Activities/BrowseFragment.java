package com.purdue.CampusFeed.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import com.purdue.CampusFeed.R;

/**
 * Created by Sean on 3/7/14.
 */

public class BrowseFragment extends Fragment {

    EventArrayAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.browse_fragment, container, false);
        /*ListView listView = new ListView(getActivity());
        adapter = new EventArrayAdapter(getActivity(), new ArrayList<Event>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int pos, long id) {
                Event e = adapter.getItem(pos);
                EventPageFragment fragment = new EventPageFragment();
                fragment.setEvent(e);
                /*getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment).commit();*/
                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                intent.putExtra(getString(R.string.START_FRAGMENT), "EventPageFragment");
                intent.putExtra(getString(R.string.EVENT),e);
                startActivity(intent);
            }
        });
        new GetEventsByTag(adapter).execute("ds");
        return listView;*/
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        viewPager.setAdapter(new BrowsePagerAdapter(((FragmentActivity) getActivity()).getSupportFragmentManager(),
                new String[]{"Social", "Sports", "Parties", "Misc"}));
    }

}