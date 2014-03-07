package com.purdue.CampusFeed;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.purdue.CampusFeed.R;
import com.facebook.*;
import com.facebook.model.*;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.res.Configuration;

public class MainActivity extends FragmentActivity {
	
   //Data members required for the drawer layout
   private String[] drawerItems;
   private DrawerLayout drawerLayout;
   private ListView drawerList;
   private ActionBarDrawerToggle drawerToggle;
   private CharSequence drawerTitle;
   
   //---------------------------------------------------
   
   //Data members required for the Facebook login
	
   //for debugging purposes
	private static final String TAG = "Facebook OAUTH";
 
	/*	Note: 
	 * 		The UiLifecycleHelper class helps to create, 
	 * 		automatically open (if applicable), save, and restore the 
	 * 		Active Session in a way that is similar to Android UI lifecycles.
	 */
	private UiLifecycleHelper uiHelper;
	
	//listens to session state changes
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	//---------------------------------------------------------------------
	
	//main onCreate
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    
	    /*this was needed to set up the login test fragment before, but
	     * now we have added all the required functions from the
	     * login test fragment to this class (but we may need this code later)
	     */
	    
	    /*if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	        loginTestFragment = new LoginTestFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, loginTestFragment)
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	    	loginTestFragment = (LoginTestFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
	    }*/
	    
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_main);
        drawerItems = getResources().getStringArray(R.array.navigationdrawer_items);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // adds cool shadow to the navigation drawer window
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        //Set custom adapter for list view
        drawerList.setAdapter(new NavigationDrawer_ArrayAdapter(this, drawerItems));

        //set the nav drawer lists' click listener
        //drawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        /* ActionBarDrawerToggle ties together the the proper interactions
        	between the sliding drawer and the action bar app icon
        */
        drawerTitle = getTitle();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){

            //Called when a drawer has settled in a completely closed state
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }

            // Called when a drawer has settled in a completely open state 
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }
        };

        //Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);
	    
	}	
	
	/*
	 * 
	 * 
	 * Functions for navigation drawer
	 * 
	 *
	 */
	
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //sync the toggle state after onRestoreInstanceState
        drawerToggle.syncState();
    }
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

   public boolean onOptionsItemSelected(MenuItem item) {
        //Pass the event to ActionBarDrawerToggle, if it returns
        //true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
        	//Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show();
            return true;
        }
        //Handle other action bar items

        return super.onOptionsItemSelected(item);
    }

   //crashes app for some reason, ask Sean
    // Called whenever we call invalidateOptionsMenu() 
   /*public boolean onPrepareOptionsMenu(Menu menu) {
        //If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerLayout);
        //menu.findItem(R.id.action_websearch.setVisible(!drawerOpen));
        return super.onPrepareOptionsMenu(menu);
    }*/

   //---------------------------------------------------------------------------------
   
   /*
    * 
    * 
    * 
    * Functions for Facebook login
    * 
    * 
    * 
    */
	
	//what to do when the session status changes (logged in or logged out)
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        Toast.makeText(this, session.getAccessToken(), Toast.LENGTH_SHORT).show();
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	    }
	}
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	//-------------------------------------------------------------
   
}


