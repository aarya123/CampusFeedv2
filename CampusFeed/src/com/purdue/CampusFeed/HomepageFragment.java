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
public class HomepageFragment extends Fragment {
    private ListView lv1;
    private ListView lv2;
    String[] events1 = new String[] {"House Party", "International Food Fair", "Mad Hatter Tea Party"};
    String[] events2 = new String[] {"YoYoMa Concert", "Tedx Purdue", "Music Festival"};

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


        RowGenerator_ArrayAdapter adapter1 = new RowGenerator_ArrayAdapter(getActivity(), events1);
        RowGenerator_ArrayAdapter adapter2 = new RowGenerator_ArrayAdapter(getActivity(), events2);

        lv1.setAdapter(adapter1);
        lv2.setAdapter(adapter2);
        lv1.setOnItemClickListener(itemClickedListener);
        lv2.setOnItemClickListener(itemClickedListener);
    }
    ListView.OnItemClickListener itemClickedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            String text = (String) adapterView.getItemAtPosition(pos);
            Toast.makeText(getActivity(), text + " selected", Toast.LENGTH_SHORT).show();
        }
    };
}