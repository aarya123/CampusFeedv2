package com.purdue.CampusFeed.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.purdue.CampusFeed.R;

/**
 * Created by Sean on 3/30/14.
 */
public class SingleFragmentActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_framelayout);

        //Open the Fragment passed to this Activity
        Fragment fragToOpen = (Fragment) getIntent().getSerializableExtra("com.purdue.CampusFeed.Activities.StartFragment");
        getFragmentManager().beginTransaction().add(R.id.basic_contentframe, fragToOpen).commit();
    }
}