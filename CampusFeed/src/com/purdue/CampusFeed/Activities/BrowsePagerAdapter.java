package com.purdue.CampusFeed.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.purdue.CampusFeed.API.AdvSearchQuery;
import com.purdue.CampusFeed.Utils.Utils;

/**
 * User: AnubhawArya
 * Date: 3/31/14
 * Time: 12:00 AM
 */
public class BrowsePagerAdapter extends FragmentStatePagerAdapter {

    public BrowsePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public int getCount() {
        return Utils.categories.length;
    }

    public CharSequence getPageTitle(int position) {
        return Utils.categories[position];
    }

    public Fragment getItem(int i) {
        AdvSearchQuery query = new AdvSearchQuery();
        query.addCategory(Utils.categories[i]);
        Bundle args = new Bundle();
        args.putParcelable("query", query);
        EventListFragment frag = new EventListFragment();
        frag.setArguments(args);
        return frag;
    }
}
