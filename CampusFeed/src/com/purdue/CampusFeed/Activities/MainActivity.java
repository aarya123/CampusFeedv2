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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.purdue.CampusFeed.Adapters.NavigationArrayAdapter;
import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.Utils.Utils;

import java.util.HashMap;

public class MainActivity extends AnimationActivity {

    public static MenuItem searchWidget_menuItem;
    private static HashMap<Frag, Fragment> fragments = new HashMap<Frag, Fragment>();
    //Data members required for the drawer layout
    private String[] drawerItems;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    //Data members for search widget
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.init(getApplicationContext());
        fragments.put(Frag.HOME, new HomepageFragment());
        fragments.put(Frag.BROWSE, new BrowseFragment());
        fragments.put(Frag.CREATE, new CreateEventFragment());
        fragments.put(Frag.SEARCH, new AdvancedSearchFragment());
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragments.get(Frag.HOME)).addToBackStack(null).commit();
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
        drawerLayout.setFocusableInTouchMode(false); //allow onBackPressed() override to work when nav drawer is open
    }
    /*
     *
	 * 
	 * Functions for navigation drawer
	 * 
	 *
	 */

    public void onBackPressed() {
        //open nav drawer before exiting application
        if(drawerLayout.isDrawerOpen(Gravity.LEFT))
        {
            finish();
        }
        else{
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    //Fragment swapping
    private void selectItem(int position) {
        Fragment fragToDisplay = null;
        switch (position) {
            case 1:
                fragToDisplay = fragments.get(Frag.HOME);
                break;
            case 2:
                fragToDisplay = fragments.get(Frag.BROWSE);
                break;
            case 3:
                fragToDisplay = fragments.get(Frag.CREATE);
                break;
            case 4:
                fragToDisplay = fragments.get(Frag.SEARCH);
                break;
            default:
                fragToDisplay = fragments.get(Frag.HOME);
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragToDisplay).addToBackStack(null).commit();
        Toast.makeText(this, "Drawer close handled in my code", Toast.LENGTH_SHORT).show();
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

    private enum Frag {
        HOME, BROWSE, CREATE, SEARCH
    }
}