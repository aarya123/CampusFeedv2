package com.purdue.CampusFeed.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.facebook.widget.LoginButton;
import com.purdue.CampusFeed.R;


public class NavigationArrayAdapter extends ArrayAdapter<String> {
    private final Context context;

    //items in the navigation drawer
    private final String[] drawerItems;

    //constructor
    public NavigationArrayAdapter(Context context, String[] values) {
        super(context, R.layout.items_in_nav_drawer, values);
        this.context = context;
        this.drawerItems = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.items_in_nav_drawer, parent, false);

        if (position == 0) {
            TextView elementInList = (TextView) view.findViewById(R.id.textView1);
            elementInList.setVisibility(8); //hides element
        } else {
            LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
            authButton.setVisibility(8);//hides button
            TextView elementInList = (TextView) view.findViewById(R.id.textView1);
            elementInList.setText(drawerItems[position]);
        }
        return view;
    }
} 