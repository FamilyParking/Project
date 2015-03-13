package it.familiyparking.app.utility;

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

import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;

/**
 * Created by francesco on 13/02/15.
 */

public class ServiceAPI extends Service{

    public ServiceAPI(){}

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("ServiceAPI","onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("ServiceAPI","onBind");
        return null;
    }

    public int onStartCommand(Intent i, int flags, int start){
        Log.e("ServiceAPI","onStartCommand");
        return START_STICKY;
    }

}