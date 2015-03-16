package it.familiyparking.app.parky;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.IpoteticPark;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 13/02/15.
 */

public class ServiceStatistic extends Service{

    public ServiceStatistic(){}

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if((intent != null) && (intent.getAction() != null)) {
            Log.e("ServiceStatistic", intent.getAction());
            Log.e("ServiceStatistic", Integer.toString(intent.getIntExtra("notification_ID",-1)));

            if(intent.getAction().equals(Code.ACTION_SAVE)){
                Log.e("ServiceStatistic", "SAVE");
            }
            else if(intent.getAction().equals(Code.ACTION_DISCARD)){
                Log.e("ServiceStatistic", "DISCARD");
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

}