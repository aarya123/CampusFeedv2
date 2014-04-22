package com.purdue.CampusFeed.Activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import com.purdue.CampusFeed.Adapters.NavigationArrayAdapter;
import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.Utils.Utils;

public class MainActivity extends AnimationActivity {

    public static MenuItem searchWidget_menuItem;

    //Data members required for the drawer layout
    private String[] drawerItems;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    //Data members for search widget
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
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

        HomepageFragment homepageFragment = new HomepageFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, homepageFragment).commit();

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

            public boolean onQueryTextChange(String newText) {
                return false;
            }

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