package com.purdue.CampusFeed.Activities;

import com.purdue.CampusFeed.R;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class SearchableActivity extends ListActivity {
	
	ListView list;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.simple_search);

	    //collapse the search widget in the main activity
	    MainActivity.searchWidget_menuItem.collapseActionView();
	    
	    // Get the intent, verify the action and get the query
	    /*Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      doMySearch(query);
	    }*/
	    handleIntent(getIntent()); 
	}
	
	public void doMySearch(String query){
		Toast.makeText(this, "Query: "+query, Toast.LENGTH_LONG).show();
	}

	public void onNewIntent(Intent intent) { 
		setIntent(intent); 
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

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.simple_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        return true;
    }
}
