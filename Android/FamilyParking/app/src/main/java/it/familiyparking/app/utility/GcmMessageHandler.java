package it.familiyparking.app.utility;

/**
 * Created by Nazzareno on 17/02/15.
 */

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.User;

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

        String user = extras.getString("User");
        String car = extras.getString("Car");
        String car_id = extras.getString("ID_car");
        String type = extras.getString("Type");

        String message = "";
        if(type.equals(Code.TYPE_GROUP))
            message = user + " added you to " + car;
        else if(type.equals(Code.TYPE_PARK))
            message = user + " parked the " + car;

        SQLiteDatabase db = Tools.getDB_Readable(this);
        if(UserTable.getNotification(db)) {
            Tools.sendNotification(this, message, car_id);
        }

        db.close();

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

}