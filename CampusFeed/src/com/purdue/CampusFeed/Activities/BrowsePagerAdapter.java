package com.purdue.CampusFeed.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.purdue.CampusFeed.API.AdvSearchQuery;

/**
 * User: AnubhawArya
 * Date: 3/31/14
 * Time: 12:00 AM
 */
public class BrowsePagerAdapter extends FragmentStatePagerAdapter {

    String[] categories;

    public BrowsePagerAdapter(FragmentManager fm, String[] categories) {
        super(fm);
        this.categories = categories;
    }

    public int getCount() {
        return categories.length;
    }

    public CharSequence getPageTitle(int position) {
        return categories[position];
    }

    public Fragment getItem(int i) {
        AdvSearchQuery query = new AdvSearchQuery();
        query.addCategory(categories[i]);
        Bundle args = new Bundle();
        args.putParcelable("query", query);
        EventListFragment frag = new EventListFragment();
        frag.setArguments(args);
        return frag;
    }
}
