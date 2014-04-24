package com.purdue.CampusFeed.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.AsyncTasks.Login;
import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.Utils.Utils;

/**
 * Created by Sean on 4/5/14.
 */
public class AnimationActivity extends FragmentActivity {

    private static final String TAG = "Facebook OAUTH";
    UiLifecycleHelper uiHelper;
    Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.sliderightin, R.anim.sliderightout);
    }

    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slideleftin, R.anim.slideleftout);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        final Api api = Api.getInstance(this);
        if (Utils.getLoginCredentials(this))
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    Log.i("TEST", "" + api.login(Utils.facebook_userID, Utils.facebook_accessToken));
                    return null;
                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //what to do when the session status changes (logged in or logged out)
    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            Utils.facebook_accessToken = session.getAccessToken();


            // If the session is open, make an API call to get user data
            // and define a new callback to handle the response
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (user != null) {
                            Utils.facebook_userID = user.getId();//user id
                            Log.d("PRANAV", "facebookId: " + Utils.facebook_userID + ", gcmId: \n" + Utils.gcmRegid);

                            new Login(AnimationActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Utils.facebook_userID, Utils.facebook_accessToken);


                            new AsyncTask() {
                                protected Object doInBackground(Object[] params) {
                                    String msg = "";
                                    // try {

                                    //register for gcm
                                    Api api = Api.getInstance(getApplicationContext());
                                    api.registerGCM(Utils.facebook_userID, Utils.gcmRegid);

                                    /*} catch (IOException ex) {
                                        msg = "Error :" + ex.getMessage();
                                        Log.d("PRANAV", ex.toString());
                                    }*/
                                    return msg;
                                }

                                @Override
                                protected void onPostExecute(Object msg) {
                                    Log.d("PRANAV", "called registerGCM!!!");
                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                        }
                    }
                }
            });
            Request.executeBatchAsync(request);
            Utils.saveLoginCredential(this);
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            Toast.makeText(getApplicationContext(), "logged out :(", Toast.LENGTH_LONG).show();
            Utils.removeLoginCredentials(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

}
