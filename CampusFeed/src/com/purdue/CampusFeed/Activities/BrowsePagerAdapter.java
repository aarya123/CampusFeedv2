package com.purdue.CampusFeed.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categories[position];
    }

    @Override
    public Fragment getItem(int i) {
        return null;
    }
}
