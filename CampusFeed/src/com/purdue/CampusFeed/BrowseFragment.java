package com.purdue.CampusFeed;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Sean on 3/7/14.
 */

public class BrowseFragment extends Fragment {
    private ListView lv;

    String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
            "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
            "Linux", "OS/2" };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.browse_layout, container, false);
    }
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //move list view initialization here
        lv = (ListView) getActivity().findViewById(R.id.browse_listview);
        RowGenerator_ArrayAdapter adapter = new RowGenerator_ArrayAdapter(getActivity(), values);
        if(lv==null)
        {
            Toast.makeText(getActivity(),"list view is null", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            lv.setAdapter(adapter); 
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                /*String text = (String) adapterView.getItemAtPosition(pos);
                Toast.makeText(getActivity(), text + " selected", Toast.LENGTH_SHORT).show();*/
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new EventPageFragment()).commit();
            }
        });
    }
}