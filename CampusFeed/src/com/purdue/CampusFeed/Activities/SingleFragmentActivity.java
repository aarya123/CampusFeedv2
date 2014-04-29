package com.purdue.CampusFeed.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.purdue.CampusFeed.API.AdvSearchQuery;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.Utils.Utils;

/**
 * Created by Sean on 3/30/14.
 */
public class SingleFragmentActivity extends AnimationActivity implements
        GestureDetector.OnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    public static long event_id = 0;
    private GestureDetectorCompat detector;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_framelayout);
        detector = new GestureDetectorCompat(this, this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Log.e("tag", getIntent().getData().toString());
        if (getIntent().hasExtra("update")) {
            // get the event with api
            getEvent getter = new getEvent(getApplicationContext(), SingleFragmentActivity.event_id);

            getter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null);
            return;

        }


        if (getIntent().getData() != null) {
            String data = getIntent().getData().toString();
            if (data.startsWith(getString(R.string.redirection_url))) {
                loadRedirectionFragment(data);
                return;
            }
        }
        if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_SEARCH)) {
            String title = getIntent().getStringExtra(SearchManager.QUERY);
            AdvSearchQuery query = new AdvSearchQuery();
            query.setTitle(title);
            EventListFragment listFragment = new EventListFragment();
            Bundle args = new Bundle();
            args.putParcelable("query", query);
            listFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().add(R.id.basic_contentframe, listFragment).commit();
            return;
        }

        //Open the Fragment passed to this Activity
        String fragToOpen = (String) getIntent().getStringExtra(getString(R.string.START_FRAGMENT));
        if (fragToOpen.equals("CreateEventFragment")) {
            CreateEventFragment fragment = new CreateEventFragment();
            Bundle eventBundle;
            if(getIntent().getBundleExtra("eventBundle") != null){
                eventBundle = getIntent().getBundleExtra("eventBundle");
                //Bundle args = new Bundle();
                //args.putParcelable("event", event);
                //args.putSerializable("event", myEvent);
                fragment.setArguments(eventBundle);
            }
            getSupportFragmentManager().beginTransaction().add(R.id.basic_contentframe, fragment).commit();
        } else if (fragToOpen.equals("EventPageFragment")) {
            Event fragEvent = (Event) getIntent().getParcelableExtra(getString(R.string.EVENT));
            //Event fragEvent = (Event) getIntent().getSerializableExtra(getString(R.string.EVENT));
            EventPageFragment fragment = new EventPageFragment();
            fragment.setEvent(fragEvent);
            // getFragmentManager().beginTransaction().setCustomAnimations(R.anim.sliderightin, R.anim.sliderightout);
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if( getCallingActivity().toString().equals("MainActivity") ){
                Toast.makeText(this, "Calling activty is MainActivity", Toast.LENGTH_SHORT).show();
                    ft.setCustomAnimations(R.anim.slideleftin, R.anim.slideleftout);
            }
                    ft.replace(R.id.basic_contentframe, fragment).commit();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Up/Home buton
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        detector.onTouchEvent(e);
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    public void onSwipeRight() {
        //Toast.makeText(this, "Swiped Right", Toast.LENGTH_SHORT).show();
        //getFragmentManager().beginTransaction().add(R.id.basic_contentframe, fragment).commit();
        onBackPressed();
    }

    public void onSwipeLeft() {

    }

    public void onSwipeBottom() {

    }

    public void onSwipeTop() {

    }


    public void loadRedirectionFragment(String data) {
        final int eventId = Integer.parseInt(data.substring(getString(R.string.redirection_url).length()));
        final Api api = Api.getInstance(getBaseContext());
        if (Utils.getLoginCredentials(this))
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    if (!api.isLoggedIn())
                        Log.i("TEST", "" + api.login(Utils.facebook_userID, Utils.facebook_accessToken));
                    return null;
                }

            }.execute();
        new AsyncTask<Void, Void, Event>() {
            protected Event doInBackground(Void... params) {
                return api.getEvent(eventId);
            }

            public void onPostExecute(Event result) {
                if (result == null) {
                    Log.e("tag", "NULLLLLLLLLLL");
                }
                EventPageFragment fragment = new EventPageFragment();
                fragment.setEvent(result);
                getSupportFragmentManager().beginTransaction().add(R.id.basic_contentframe, fragment).commit();
            }
        }.execute();

    }

    @Override
    public void onNewIntent(Intent i) {
        Log.i("TEST", i.getAction());
    }

    public class getEvent extends AsyncTask<String[], Void, Event> {
        Context c;
        long event_id = 0;
        Event event;

        public getEvent(Context c, long event_id) {
            this.c = c;
            this.event_id = event_id;

        }

        protected Event doInBackground(String[]... params) {
            Api api = Api.getInstance(c);
            return api.getEvent(event_id);
        }

        protected void onPostExecute(Event event) {
            Log.d("CampusFeed", "gotten event");


            EventPageFragment fragment = new EventPageFragment();
            fragment.setEvent(event);
            getSupportFragmentManager().beginTransaction().add(R.id.basic_contentframe, fragment).commit();

        }


    }


}