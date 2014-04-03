package com.purdue.CampusFeed.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.purdue.CampusFeed.Adapters.NavigationArrayAdapter;
import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.AsyncTasks.Login;
import com.purdue.CampusFeed.Utils.Utils;

import java.io.Serializable;

public class MainActivity extends FragmentActivity {

    //Data members required for the drawer layout
    private String[] drawerItems;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    //private FragmentManager fragmentManager;

    //------------------------------------------------------------------------

    //Data members required for the Facebook login

    public static String facebook_userID;
    public static String SERVER_LONG_TOKEN;
    public static String facebook_profileName;
    public static String facebook_accessToken;

    
    //Data members for search widget
    
    public static MenuItem searchWidget_menuItem;
    
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
        Utils.init(getApplicationContext());
        
        HomepageFragment homepageFragment = new HomepageFragment();
        //fragmentManager = getSupportFragmentManager();
        getFragmentManager().beginTransaction().add(R.id.content_frame, homepageFragment).commit();

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        drawerItems = getResources().getStringArray(R.array.navigationdrawer_items);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // adds cool shadow to the navigation drawer window
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        //Set custom adapter for list view
        drawerList.setAdapter(new NavigationArrayAdapter(this, drawerItems));

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            	selectItem(pos);
            
            }
        });

        //set the nav drawer lists' click listener
        //drawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        /* ActionBarDrawerToggle ties together the the proper interactions
            between the sliding drawer and the action bar app icon
        */
        drawerTitle = getTitle();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            //Called when a drawer has settled in a completely closed state
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }

            // Called when a drawer has settled in a completely open state 
            public void onDrawerOpened(View drawerView) {
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

    //Fragment swapping
    private void selectItem(int position) {
        android.app.Fragment fragToDisplay = null;
        switch (position) {
            case 1:
            	
                fragToDisplay = new HomepageFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragToDisplay).commit();
                break;
            case 2:

                fragToDisplay = new BrowseFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragToDisplay).commit();
                break;
            case 3:
            	
                fragToDisplay = new CreateEventFragment();
                Intent intent  = new Intent(this, SingleFragmentActivity.class);
                intent.putExtra(getString(R.string.START_FRAGMENT), "CreateEventFragment");
                startActivity(intent);
                break;
            case 4:
        
                fragToDisplay = new AdvancedSearch_Fragment();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragToDisplay).commit();
                break;
            default:
                break;
        }
        
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //sync the toggle state after onRestoreInstanceState
        drawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
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
    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            facebook_accessToken = session.getAccessToken();
            //Toast.makeText(this, "A, Toast.LENGTH_SHORT).show();

            // If the session is open, make an API call to get user data
            // and define a new callback to handle the response
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (user != null) {
                            facebook_userID = user.getId();//user id
                            facebook_profileName = user.getName();//user's profile name
                            Toast.makeText(getApplicationContext(), "User ID: " + facebook_userID + "\n\nName: " + facebook_profileName + "\n\nAccess token: " + facebook_accessToken, Toast.LENGTH_LONG).show();
                            //userNameView.setText(user.getName());
                            Login l = new Login();
                            l.execute("login");


                        }
                    }
                }
            });
            Request.executeBatchAsync(request);


        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            Toast.makeText(getApplicationContext(), facebook_profileName + " logged out :(", Toast.LENGTH_LONG).show();
            facebook_userID = null;
            facebook_profileName = null;
            facebook_accessToken = null;

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
    
  //setting up the search widget
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        //stores the menu item
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