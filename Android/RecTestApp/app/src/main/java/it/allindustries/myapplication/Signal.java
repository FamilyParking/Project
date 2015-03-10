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

    private int counter = 0;
    private boolean dataConcistence = false;
    private int counter_consecutive_walking = 0;
    private int counter_consecutive_vehicle = 0;
    private int currentState = DetectedActivity.STILL;

    //DetectedActivity.IN_VEHICLE   -->     0
    //DetectedActivity.ON_FOOT      -->     2
    //DetectedActivity.STILL        -->     3
    //DetectedActivity.UNKNOWN      -->     4
    //DetectedActivity.TILTING      -->     5
    //DetectedActivity.WALKING      -->     7


    protected void onHandleIntent(Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            ActivityRecognitionResult activityRecognitionResult = bundle.getParcelable("com.google.android.location.internal.EXTRA_ACTIVITY_RESULT");
            //Log.e("Signal", activityRecognitionResult.toString());

            DetectedActivity detectedActivity = activityRecognitionResult.getMostProbableActivity();

            SamplesTable.insertSamples(this,detectedActivity.getType(),detectedActivity.toString());

            if((detectedActivity.getType() == DetectedActivity.ON_FOOT) || (detectedActivity.getType() == DetectedActivity.RUNNING) || (detectedActivity.getType() == DetectedActivity.WALKING)){
                if(counter_consecutive_walking == 10){
                    changeState(DetectedActivity.WALKING);

                    SamplesTable.insertSamples(this,Sample.PARKED,detectedActivity.toString());
                    notifying("Park!");
                }
                else if(currentState == DetectedActivity.IN_VEHICLE){
                    counter_consecutive_walking++;

                    if(!dataConcistence)
                        dataConcistence = true;
                }
            }
            else if(detectedActivity.getType() == DetectedActivity.IN_VEHICLE){
                if(counter_consecutive_vehicle == 10){
                    changeState(DetectedActivity.IN_VEHICLE);
                }
                if(currentState != DetectedActivity.IN_VEHICLE){
                    counter_consecutive_vehicle++;

                    if(!dataConcistence)
                        dataConcistence = true;
                }
            }

            if(dataConcistence)
                counter++;

            if(counter == 60){
                if((counter_consecutive_vehicle < 8) && (counter_consecutive_walking < 8)){
                    counter_consecutive_walking = 0;
                    counter_consecutive_vehicle = 0;
                }
            }

            //Log.e("Signal", "dataConcistence="+Boolean.toString(dataConcistence)+"{[counter="+counter+"], [counter_consecutive_vehicle="+counter_consecutive_vehicle+"], [counter_consecutive_walking="+counter_consecutive_walking+"]}");
            //Log.e("Signal","--------------------------");
        }
    }

    private void changeState(int state){
        currentState = state;
        counter = 0;
        counter_consecutive_walking = 0;
        counter_consecutive_vehicle = 0;
        dataConcistence = false;
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
