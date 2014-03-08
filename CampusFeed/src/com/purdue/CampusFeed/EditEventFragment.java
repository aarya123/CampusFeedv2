package com.purdue.CampusFeed;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sean on 3/8/14.
 */
public class EditEventFragment extends Fragment {
    private Event myEvent;

    public EditEventFragment(Event event)
    {
        super();
        myEvent = event;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_event, container, false);
    }
}