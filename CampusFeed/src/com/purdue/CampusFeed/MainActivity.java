package com.purdue.CampusFeed;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.purdue.CampusFeed.R;
import com.facebook.*;
import com.facebook.model.*;

import android.widget.TextView;
import android.content.Intent;

public class MainActivity extends FragmentActivity {

	//the menu item for the settings fragment
	private MenuItem settings;
	
	//constants for array of fragments
	private static final int SPLASH = 0;		//fragment for login screen
	private static final int SELECTION = 1;		//fragment for screen after login
	private static final int SETTINGS = 2;		//fragment for settings
	private static final int FRAGMENT_COUNT = SETTINGS +1;

	//array of fragments
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	
	/*	Note: 
	 * 		The UiLifecycleHelper class helps to create, 
	 * 		automatically open (if applicable), save, and restore the 
	 * 		Active Session in a way that is similar to Android UI lifecycles.
	 */
	private UiLifecycleHelper uiHelper;
	
	//callback is a session state change listener
	private Session.StatusCallback callback = 
	    new Session.StatusCallback() {
	    @Override
	    public void call(Session session, 
	            SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	
	/*
	 * 	hides fragments initially.
	 * 	the uiHelper decides what fragments to show.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  
		Log.d("TESTING", "in onCreate!!!!");
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// hiding the fragments initially
		FragmentManager fm = getSupportFragmentManager();
		fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
		fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
		fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		for(int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();
	    
	  
	  // old way to login to facebook (from the first tutorial)
		
	  /*Session.openActiveSession(this, true, new Session.StatusCallback() {

		  
	    // callback when session changes state
	    @SuppressWarnings("deprecation")
		@Override
		
	    public void call(Session session, SessionState state, Exception exception) {
	    	Log.d("TESTING", "in second callback!!!!");
	    	if (session.isOpened()) {
	    		
	    		Log.d("TESTING", "facebook session is open!!!!");
	    		
	    		// make request to the /me API
	    		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

	    		  // callback after Graph API response with user object
	    		  @Override
	    		  public void onCompleted(GraphUser user, Response response) {
	    			  
	    			  Log.d("TESTING", "in callback method!!!");
	    			  if (user != null) {
	    				  Log.d("TESTING", "user != null!!!!");
	    				  TextView welcome = (TextView) findViewById(R.id.welcome);
	    				  welcome.setText("Hello " + user.getName() + "!");
	    				}
	    		  }
	    		});
	    	}
	    }
	  });*/
	}

	/*
 		(may need this method later)
 	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/
	
	
	/*
	 * 	onActivityResult()
	 * 
	 *	This function is used to update the active session (user that is logged 
	 *	in through facebook)
	 *
	 *	the 'Session' class is part of the facebook SDK and it manages most of
	 *	the process of authenticating and authorizing of users
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  //Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	//shows a given fragment and hides all other fragments
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < fragments.length; i++) {
	        if (i == fragmentIndex) {
	            transaction.show(fragments[i]);
	        } else {
	            transaction.hide(fragments[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    transaction.commit();
	}
	
	//used to enable session state change checks
	private boolean isResumed = false;
	
	//sets the isResumed flag if the activity is resumed
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	}

	//clears the isResumed flag if the activity is paused
	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	//shows the relevant fragment based on the user's authenticated state 
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    // Only make changes if the activity is visible
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	       
	        // Clear the back stack 
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	            // If the session state is open:
	            // Show the authenticated fragment
	            showFragment(SELECTION, false);
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            showFragment(SPLASH, false);
	        }
	    }
	}
	
	/*
	 * similar to onSessionStateChange, but this 
	 * handles the case where the fragments are 
	 * newly instantiated
	 */
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    Session session = Session.getActiveSession();

	    if (session != null && session.isOpened()) {
	        // if the session is already open,
	        // try to show the selection fragment
	        showFragment(SELECTION, false);
	    } else {
	        // otherwise present the splash screen
	        // and ask the person to login.
	        showFragment(SPLASH, false);
	    }
	}
	
	//adds the options menu only if the user is logged in and the 'selection' fragment is being displayed
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	    // only add the menu when the selection fragment is showing
	    if (fragments[SELECTION].isVisible()) {
	        if (menu.size() == 0) {
	            settings = menu.add(R.string.settings);
	        }
	        return true;
	    } else {
	        menu.clear();
	        settings = null;
	    }
	    return false;
	}
	
	//used to display the user settings fragment when the settings menu is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.equals(settings)) {
	        showFragment(SETTINGS, true);
	        return true;
	    }
	    return false;
	}
	
	

	

}


