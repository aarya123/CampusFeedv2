/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purdue.CampusFeed.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.purdue.CampusFeed.Activities.ContactDetailActivity;
import com.purdue.CampusFeed.Activities.ContactsListActivity;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class contains static utility methods.
 */
public class Utils {

    //---------GCM variables------------------
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String TAG = "CampusFeed";
    /**
     * Tag used on log messages.
     */
    static final String GCM_DEBUG_TAG = "GCMDemo";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    static String SENDER_ID = "872065754556";
    static GoogleCloudMessaging gcm;
    static String gcmRegid;
    private static ImageLoader mImageLoader;
    TextView mDisplay;
    AtomicInteger msgId = new AtomicInteger();

    //--------------------------------------//

    //---------------GCM Functions----------//
    SharedPreferences prefs;
    Context context;

    // Prevents instantiation.
    private Utils() {
    }

    //-----------------------------------------//

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    @SuppressWarnings("unchecked")
    private static void registerInBackground(final Context context) {
        new AsyncTask() {
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    gcmRegid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + gcmRegid;


                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //sendRegistrationIdToBackend();

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    gcmRegid = "FAIL";
                    Log.d("PRANAV", ex.toString());
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Object msg) {
                Toast.makeText(context, "id = " + gcmRegid, Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private static void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    public static boolean checkPlayServices(Context context) {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    public static void init(Context c) {
        //init contact stuff
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                c).defaultDisplayImageOptions(defaultOptions).build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);


        //init GCM stuff (added by Pranav)

        if (checkPlayServices(c)) {
            //register for GCM everytime, crappy implementation
            //will change later

            gcm = GoogleCloudMessaging.getInstance(c);
            registerInBackground(c);
        } else
            Log.i(GCM_DEBUG_TAG, "No valid Google Play Services APK found.");
    }

    public static ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * Enables strict mode. This should only be called when debugging the
     * application and is useful for finding some potential bugs or best
     * practice violations.
     */
    @TargetApi(11)
    public static void enableStrictMode() {
        // Strict mode is only available on gingerbread or later
        if (Utils.hasGingerbread()) {

            // Enable all thread strict mode policies
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder()
                    .detectAll().penaltyLog();

            // Enable all VM strict mode policies
            StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder()
                    .detectAll().penaltyLog();

            // Honeycomb introduced some additional strict mode features
            if (Utils.hasHoneycomb()) {
                // Flash screen when thread policy is violated
                threadPolicyBuilder.penaltyFlashScreen();
                // For each activity class, set an instance limit of 1. Any more
                // instances and
                // there could be a memory leak.
                vmPolicyBuilder.setClassInstanceLimit(
                        ContactsListActivity.class, 1).setClassInstanceLimit(
                        ContactDetailActivity.class, 1);
            }

            // Use builders to enable strict mode policies
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    /**
     * Uses static final constants to detect if the device's platform version is
     * Gingerbread or later.
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Uses static final constants to detect if the device's platform version is
     * Honeycomb or later.
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Uses static final constants to detect if the device's platform version is
     * Honeycomb MR1 or later.
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * Uses static final constants to detect if the device's platform version is
     * ICS or later.
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }
}
