package it.familiyparking.app.utility;

/**
 * Created by Nazzareno on 17/02/15.
 */

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService {

    String mes;
    private Handler handler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

<<<<<<< Updated upstream
        mes = extras.getString("title");
        Log.e("GCM", "Received : (" + messageType + ")  " + extras.getString("name") + "[" + extras.getString("type") + "]");
        Tools.sendNotification(this, extras.getString("name"), extras.getString("type"));
=======
<<<<<<< HEAD
        String user = extras.getString("User");
        String car = extras.getString("Car");

        String type = extras.getString("Type");
        if(type.equals(Code.TYPE_GROUP))
            message = user + " added you to " + car;
        else if(type.equals(Code.TYPE_PARK))
            message = user + " parked the " + car;

        Log.e("GCM Notification",message);

        Tools.sendNotification(this, message);
=======
        mes = extras.getString("title");
        Log.e("GCM", "Received : (" + messageType + ")  " + extras.getString("name") + "[" + extras.getString("type") + "]");
        Tools.sendNotification(this, extras.getString("name"), extras.getString("type"));
>>>>>>> origin/master
>>>>>>> Stashed changes

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

}