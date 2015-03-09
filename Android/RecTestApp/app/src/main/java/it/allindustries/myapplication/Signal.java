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

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by mauropiva on 06/03/15.
 */
public class Signal extends IntentService {

    private static int counter = 0;
    private static int lastActivity = DetectedActivity.STILL;

    protected void onHandleIntent(Intent intent) {
        //Log.e("Signal","Something Appened " + counter++);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            ActivityRecognitionResult activityRecognitionResult = bundle.getParcelable("com.google.android.location.internal.EXTRA_ACTIVITY_RESULT");
            //Log.e("Signal", activityRecognitionResult.toString());

            DetectedActivity detectedActivity = activityRecognitionResult.getMostProbableActivity();

            if(detectedActivity.getType() == DetectedActivity.IN_VEHICLE) {
                lastActivity = DetectedActivity.IN_VEHICLE;
            }
            else if((detectedActivity.getType() == DetectedActivity.ON_FOOT) || (detectedActivity.getType() == DetectedActivity.RUNNING) || (detectedActivity.getType() == DetectedActivity.WALKING)){
                if(lastActivity == DetectedActivity.IN_VEHICLE){
                    SamplesTable.insertSamples(this,activityRecognitionResult.toString());
                }
            }

        }
    }

    public Signal(){
        super("Signal");
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
