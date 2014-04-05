package com.purdue.CampusFeed.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.purdue.CampusFeed.API.AdvSearchQuery;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;
import com.purdue.CampusFeed.AsyncTasks.SearchQueryExecutor;
import com.purdue.CampusFeed.R;

import java.util.ArrayList;

/**
 * Created by Sean on 3/7/14.
 */

public class AdvancedSearch_Fragment extends Fragment {

    EventArrayAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.advanced_search, container, false);
        Button searchButton = (Button) view.findViewById(R.id.searchButton);
        ListView resultsView = (ListView) view.findViewById(R.id.search_list);
        adapter = new EventArrayAdapter(getActivity(), new ArrayList<Event>());

        resultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Event e = (Event) adapterView.getAdapter().getItem(pos);
                EventPageFragment fragment = new EventPageFragment();
                fragment.setEvent(e);
                //getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                intent.putExtra(getString(R.string.START_FRAGMENT), "EventPageFragment");
                intent.putExtra(getString(R.string.EVENT), e);
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText text = (EditText) getActivity().findViewById(R.id.searchName);
                AdvSearchQuery query = new AdvSearchQuery();
                query.setTitle(text.getText().toString());
                new SearchQueryExecutor(AdvancedSearch_Fragment.this.getActivity(), adapter).execute(query);
            }
        });
        return view;
    }
}






