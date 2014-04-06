package com.purdue.CampusFeed.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.purdue.CampusFeed.API.Api;
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
        if(getIntent().getData() != null) {
        	String data = getIntent().getData().toString();
        	if(data.startsWith(getString(R.string.redirection_url))) {
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
    	final int eventId = Integer.parseInt(data.substring(getString(R.string.redirection_url).length()));
    	final Api api = Api.getInstance(getBaseContext());
    	if (api.getLogin() != null) {
    		new AsyncTask<Void, Void, Event>() {

				@Override
				protected Event doInBackground(Void... params) {
					return api.getEvent(eventId);
				}
				
				@Override
				public void onPostExecute(Event result) {
					if (result == null) {
		    			Log.e("tag", "NULLLLLLLLLLL");
		    		}
		            EventPageFragment fragment = new EventPageFragment();
		            fragment.setEvent(result);
		            getFragmentManager().beginTransaction().add(R.id.basic_contentframe, fragment).commit();
				}
    			
    		}.execute();
    		
    	} else {
    		Intent intent = new Intent(getBaseContext(), MainActivity.class);
	        startActivity(intent);
    	}
    }
    
    
}