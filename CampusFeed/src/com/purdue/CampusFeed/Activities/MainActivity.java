package com.purdue.CampusFeed.Activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.Adapters.NavigationArrayAdapter;
import com.purdue.CampusFeed.AsyncTasks.Login;
import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.Utils.Utils;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "Facebook OAUTH";
    //Data members required for the Facebook login
    public static MenuItem searchWidget_menuItem;
    String facebook_userID, facebook_accessToken, facebook_profileName;
    //Data members required for the drawer layout
    private String[] drawerItems;
    private DrawerLayout drawerLayout;
    private ListView drawerList;


    //Data members for search widget
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
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
    //setting up the search widget
    private String query;

	/*
     *
	 * 
	 * Functions for navigation drawer
	 * 
	 *
	 */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.init(getApplicationContext());
        final Api api = Api.getInstance(this);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Log.i("TEST", "" + api.login("1", "1"));
                return null;
            }

        }.execute();

        HomepageFragment homepageFragment = new HomepageFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, homepageFragment).commit();

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

    //Fragment swapping
    private void selectItem(int position) {
        Fragment fragToDisplay;
        switch (position) {
            case 1:

                fragToDisplay = new HomepageFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragToDisplay).commit();
                break;
            case 2:

                fragToDisplay = new BrowseFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragToDisplay).commit();
                break;
            case 3:

                fragToDisplay = new CreateEventFragment();
                Intent intent = new Intent(this, SingleFragmentActivity.class);
                intent.putExtra(getString(R.string.START_FRAGMENT), "CreateEventFragment");
                startActivity(intent);
                break;
            case 4:

                fragToDisplay = new AdvancedSearchFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragToDisplay).commit();
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

   /*
    *
    * Functions for Facebook login
    *
    */

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

    //what to do when the session status changes (logged in or logged out)
    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            facebook_accessToken = session.getAccessToken();


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
                            Log.d("PRANAV", "facebookId: " + facebook_userID + ", gcmId: \n" + Utils.gcmRegid);

                            new Login(MainActivity.this).execute(facebook_userID, facebook_accessToken);


                            new AsyncTask() {
                                protected Object doInBackground(Object[] params) {
                                    String msg = "";
                                    // try {

                                    //register for gcm
                                    Api api = Api.getInstance(getApplicationContext());
                                    api.registerGCM(facebook_userID, Utils.gcmRegid);

                                    /*} catch (IOException ex) {
                                        msg = "Error :" + ex.getMessage();
                                        Log.d("PRANAV", ex.toString());
                                    }*/
                                    return msg;
                                }

                                @Override
                                protected void onPostExecute(Object msg) {
                                    Log.d("PRANAV", "called registerGCM!!!");
                                }
                            }.execute(null, null, null);
                        }
                    }
                }
            });
            Request.executeBatchAsync(request);


        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            Toast.makeText(getApplicationContext(), "logged out :(", Toast.LENGTH_LONG).show();
            facebook_userID = null;
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
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SingleFragmentActivity.class)));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_SEARCH);
                i.setClass(MainActivity.this, SingleFragmentActivity.class);
                i.putExtra(SearchManager.QUERY, query);
                startActivity(i);
                return true;
            }

        });
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        return true;
    }
}