package com.purdue.CampusFeed.Activities;

import java.util.ArrayList;

import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.API.AdvSearchQuery;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.Adapters.EventArrayAdapter;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class SearchableActivity extends Activity {
	
	EventArrayAdapter adapter;
	ListView lv;
	private MenuItem searchWidget_menuItem;
    static ArrayList<Event> events = new ArrayList<Event>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.simple_search);
	    lv = (ListView) findViewById(R.id.searchWidget_listView);
    	
	    //set action bar title
	    setTitle("Search Results");
	    
	    //collapse the search widget in the main activity
	    MainActivity.searchWidget_menuItem.collapseActionView();
		
	    handleIntent(getIntent()); 
	}
	
	public void doMySearch(String query){
		Toast.makeText(this, "Query: "+query, Toast.LENGTH_LONG).show();
	    
		//events = Api.advSearchEvent(query);
		
	    //set the adapter and generate the rows
		adapter = new EventArrayAdapter(this, events);

		//new GetEventsByTag().execute("ds");
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int pos, long id) {
				/*
				 * String text = (String) adapterView.getItemAtPosition(pos);
				 * Toast.makeText(getActivity(), text + " selected",
				 * Toast.LENGTH_SHORT).show();
				 */
				Event e = events.get(pos);
				EventPageFragment fragment = new EventPageFragment();
				fragment.setEvent(e);
				getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, fragment).commit();
			}
		});
	}

	public void onNewIntent(Intent intent) { 
		setIntent(intent); 
		
		//collapse search widget
		searchWidget_menuItem.collapseActionView();
		handleIntent(intent); 
	} 

	private void handleIntent(Intent intent) { 
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) { 
			String query = intent.getStringExtra(SearchManager.QUERY); 
			doMySearch(query); 
		} 
	}    
		   
	//setting up the search widget
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        //store menu item
        searchWidget_menuItem = menu.findItem(R.id.simple_search);
        
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.simple_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        return true;
    }
}
