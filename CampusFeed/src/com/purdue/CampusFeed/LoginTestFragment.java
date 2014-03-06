package com.purdue.CampusFeed;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoginTestFragment extends Fragment {

	//for debugging purposes
	private static final String TAG = "MainFragment";
	
	
	//listens to session state changes
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	
	/*	Note: 
	 * 		The UiLifecycleHelper class helps to create, 
	 * 		automatically open (if applicable), save, and restore the 
	 * 		Active Session in a way that is similar to Android UI lifecycles.
	 */
	private UiLifecycleHelper uiHelper;
	
	//create the fragment
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	//setup the layout
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.activity_main, container, false);

	    /*
	     * Note: During the authentication flow, the results are returned back to the main activity by default. 
	     * But in order to handle the results, I allow this fragment to receive the onActivityResult() call 
	     * rather than the activity by calling the setFragment() method on the LoginButton instance
	     * 
	     * These 2 statements change the login button to the logout button if the user is logged in
	     */
	   
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    authButton.setFragment(this);
	    
	    return view;
	}
	
	//what to do when the session status changes (logged in or logged out)
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	    }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

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
}
