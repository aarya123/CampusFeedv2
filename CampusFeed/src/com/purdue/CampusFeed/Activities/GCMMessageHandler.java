package com.purdue.CampusFeed.Activities;

import java.util.Iterator;
import java.util.Set;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.purdue.CampusFeed.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.purdue.CampusFeed.Activities.MainActivity;

public class GCMMessageHandler extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "PRANAV";
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;

    public GCMMessageHandler() {
        super("GCMMessageHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Processing Notification!");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString(),extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString(),extras);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
            	
                sendNotification("An event has been updated. Click here to see!",extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, Bundle extras) {
    	
    	Log.d(TAG, "sending notification!");
       /* mNotificationManager = (NotificationManager)
          this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());*/
    	if(extras.getString("mode").equals("update_event")){
    		int event_id = extras.getInt("response");
    	
    	}
    	else
    	{
    		// invite event notification
    	}
    	
    	Intent intent = new Intent(this, MainActivity.class);
    	PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
    	Notification notification = new Notification.Builder(this).setContentTitle(this.getResources().getString(R.string.app_name))
    	            .setContentText(msg).setSmallIcon(R.drawable.ic_launcher)
    	            .setContentIntent(pIntent).getNotification();
    	NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	notificationManager.notify(0, notification);
    }
}