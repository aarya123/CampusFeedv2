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
	
	//the LoginTestFragment
	private LoginTestFragment loginTestFragment;
	
	
	//adds an instance of LoginTestFragment 
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    if (savedInstanceState == null) {
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
	    }
	}	

}


