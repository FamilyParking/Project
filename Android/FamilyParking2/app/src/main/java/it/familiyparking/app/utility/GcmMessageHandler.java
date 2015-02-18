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

        mes = extras.getString("title");
        Log.e("GCM", "Received : (" + messageType + ")  " + extras.getString("name") + "[" + extras.getString("type") + "]");
        Tools.sendNotification(this, extras.getString("name"), extras.getString("type"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

}