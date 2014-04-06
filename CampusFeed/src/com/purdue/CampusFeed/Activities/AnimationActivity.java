package com.purdue.CampusFeed.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.purdue.CampusFeed.R;

/**
 * Created by Sean on 4/5/14.
 */
public class AnimationActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.sliderightin, R.anim.sliderightout);
    }

    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slideleftin, R.anim.slideleftout);
    }
}
