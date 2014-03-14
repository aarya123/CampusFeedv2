package com.purdue.CampusFeed;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Sean on 2/25/14.
 */
/*
 * 
 * 
 * 
 * UNUSED
 * 
 * 
 * 
 * 
 */

public class NavigationDrawerActivity extends Activity {
   private String[] drawerItems;
   private DrawerLayout drawerLayout;
   private ListView drawerList;
   private ActionBarDrawerToggle drawerToggle;
   private CharSequence drawerTitle;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerItems = getResources().getStringArray(R.array.navigationdrawer_items);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        //Set adapter for list view
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItems));

        //set the nav drawer lists' click listener
        //drawerList.setOnItemClickListener(new DrawerItemClickListener());

        drawerTitle = getTitle();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){

            /*Called when a drawer has settled in a completely closed state*/
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state */
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }
        };

        //Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

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
            return true;
        }
        //Handle other action bar items

        return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    public boolean onPrepareOptionsMenu(Menu menu) {
        //If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerLayout);
        //menu.findItem(R.id.action_websearch.setVisible(!drawerOpen));
        return super.onPrepareOptionsMenu(menu);
    }
}