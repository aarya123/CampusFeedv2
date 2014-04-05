package com.purdue.CampusFeed.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.R;

/**
 * Created by Sean on 3/30/14.
 */
public class SingleFragmentActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_framelayout);
        
        //Log.e("tag", getIntent().getData().toString());
        if(getIntent().getData() != null) 
        {
        	String data = getIntent().getData().toString();
        	if(data.startsWith(getString(R.string.redirection_url)))
            {
            	loadRedirectionFragment(data);
            	return;
            }
        }
        
        //Open the Fragment passed to this Activity
        String fragToOpen = (String) getIntent().getStringExtra(getString(R.string.START_FRAGMENT));
        
        if(fragToOpen.equals("CreateEventFragment"))
        {
            CreateEventFragment fragment = new CreateEventFragment();
            getFragmentManager().beginTransaction().add(R.id.basic_contentframe, fragment).commit();
        }
        else if(fragToOpen.equals("EventPageFragment"))
        {
            Event fragEvent = (Event) getIntent().getSerializableExtra(getString(R.string.EVENT));
            EventPageFragment fragment = new EventPageFragment();
            fragment.setEvent(fragEvent);
            getFragmentManager().beginTransaction().add(R.id.basic_contentframe, fragment).commit();
        }
    }
    
    
    public void loadRedirectionFragment(String data) {
    	Log.e("tag", getIntent().getData().toString());
    	int eventId = Integer.parseInt(data.substring(getString(R.string.redirection_url).length()));  
    	Log.e("tag", eventId+"");
    }
}