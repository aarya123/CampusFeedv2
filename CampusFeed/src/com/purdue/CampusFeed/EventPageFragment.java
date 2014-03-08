package com.purdue.CampusFeed;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Sean on 2/26/14.
 */
public class EventPageFragment extends Fragment implements OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	final View fragmentView = inflater.inflate(R.layout.event_page, container, false); 
    	Button button= (Button) fragmentView.findViewById(R.id.invite_button);
        button.setOnClickListener(this);
        return fragmentView;
    }

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this.getActivity(), ContactsListActivity.class);
        startActivity(intent);
	}
    
    
}