package com.purdue.CampusFeed.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GCMReceiver extends WakefulBroadcastReceiver {

	   public void onReceive(Context context, Intent intent) {
	        // Explicitly specify that GcmIntentService will handle the intent.
		   
		   Log.d("PRANAV", "Received gcm notification!");
	        ComponentName comp = new ComponentName(context.getPackageName(),
	                GCMMessageHandler.class.getName());
	        // Start the service, keeping the device awake while it is launching.
	        startWakefulService(context, (intent.setComponent(comp)));
	        setResultCode(Activity.RESULT_OK);
	    }

}
