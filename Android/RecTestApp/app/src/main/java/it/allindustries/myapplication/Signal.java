package it.allindustries.myapplication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by mauropiva on 06/03/15.
 */
public class Signal extends IntentService {

    private static int counter = 0;
    private static String last = "1";

    protected void onHandleIntent(Intent intent) {

       // System.out.println(intent.getDataString());
       // dumpIntent(intent);

        Log.e("Signal","Something Appened " + counter++);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            String best = bundle.get("com.google.android.location.internal.EXTRA_ACTIVITY_RESULT").toString();
            Log.e("LOG_TAG", best);

            int t = best.indexOf("type=");

            String type = "" + best.charAt(t + 5);

            Log.e("Signal", last + "   " + type);

            if(type.equals("4")||type.equals("5")) return;

            if(last.equals("3") && type.equals("2")){
                //notifying("You are walking!");
            }

            if (last.equals("0") && !type.equals("0")) {
                String value = "" + best.charAt(t + 19) + best.charAt(t + 20);

                if (!(best.charAt(t + 21) == ']')) value += best.charAt(t + 21);

                notifying("You parked!");
                SamplesTable.insertSamples(this,"Dumping Intent " + type + " " + value);

            }else{
                last=type;
            }
        }
    }

    public Signal(){
        super("Segnale");
    }

    public static void dumpIntent(Intent i){

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();

            Log.e("LOG_TAG", "Dumping Intent start");

            while (it.hasNext()) {
                String key = it.next();
                Log.e("LOG_TAG","[" + key + "=" + bundle.get(key)+"]");
            }

            Log.e("LOG_TAG","Dumping Intent end");
        }
    }

    public void notifying(String note){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder.setContentTitle("Family Parking");

        notificationBuilder.setContentText(note);

        notificationBuilder.setTicker(note);
        notificationBuilder.setWhen(System.currentTimeMillis());
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        notificationBuilder.setContentIntent(contentIntent);

        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        mNotificationManager.notify(0, notificationBuilder.build());

    }
}
