package it.allindustries.myapplication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Set;
import android.os.Vibrator;

/**
 * Created by mauropiva on 06/03/15.
 */
public class Segnale extends IntentService {

    private static int counter = 0;
    private static String last = "1";

    protected void onHandleIntent(Intent intent) {

       // System.out.println(intent.getDataString());
       // dumpIntent(intent);
        System.out.println("Something Appened " + counter++);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String best = bundle.get("com.google.android.location.internal.EXTRA_ACTIVITY_RESULT").toString();
            int t = best.indexOf("type=");
            String type = "" + best.charAt(t + 5);
            System.out.println(last+"   "+type);
            if(type.equals("4")||type.equals("5")) return;
            if(last.equals("3") && type.equals("2"))notifyWalking("You are walking!");
            if (last.equals("0") && !type.equals("0")) {
                notifyWalking("You parked!");
                String value = "" + best.charAt(t + 19) + best.charAt(t + 20);
                if (!(best.charAt(t + 21) == ']')) value += best.charAt(t + 21);

                Log.e("LOG_TAG", "Dumping Intent " + type + " " + value);


                String FILENAME = "storico.txt";
                String string = "" + System.currentTimeMillis() + " " + type + " " + value + "\n";
                try {
                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
                    fos.write(string.getBytes());
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                last=type;
            }
        }
    }

    public Segnale(){
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

    public void notifyWalking(String str){
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            int icon = R.drawable.ic_launcher;
            CharSequence notiText = "You did something!";
            long meow = System.currentTimeMillis();

            Notification notification = new Notification(icon, notiText, meow);

            Context context = getApplicationContext();
            CharSequence contentTitle = "Family Parking Recognizing";
            CharSequence contentText = str;
            Intent notificationIntent = new Intent(this, OkOrNot.class);
             notificationIntent.setAction(str);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

            int SERVER_DATA_RECEIVED = 1;
            notificationManager.notify(SERVER_DATA_RECEIVED, notification);

        }
}
