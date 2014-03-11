package com.purdue.CampusFeed;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Sean on 2/26/14.
 */

public class EventPageFragment extends Fragment implements OnClickListener {
    public Event myEvent;
    
    public void setEvent(Event e) {
    	this.myEvent = e;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	final View fragmentView = inflater.inflate(R.layout.event_page, container, false); 
    	Button button= (Button) fragmentView.findViewById(R.id.inviteButton);
        button.setOnClickListener(this);
        return fragmentView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Button editButton = (Button)getActivity().findViewById(R.id.editButton);
      //  EditText descriptionText = (EditText)getActivity().findViewById(R.id.eventdescription_eventpage);
        TextView desc = (TextView)getActivity().findViewById(R.id.event_page_info);
        desc.setText(myEvent.eventName+" @ "+myEvent.datetime+"\n\n\n\n"+myEvent.eventDescription+"\n\n\n\n"+myEvent.eventLocation);


       editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new EditEventFragment(myEvent)).commit();
            }
        });
    }
	
    @Override
	public void onClick(View v) {
		Intent intent = new Intent(this.getActivity(), ContactsListActivity.class);
        startActivity(intent);
	}

}