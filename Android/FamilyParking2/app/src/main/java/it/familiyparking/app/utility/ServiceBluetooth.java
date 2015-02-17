package it.familiyparking.app.utility;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by mauropiva on 13/02/15.
 */
public class ServiceBluetooth extends Service{

    public ServiceBluetooth(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        onHandleIntent();
    }

    protected void onHandleIntent(){
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("name", device.getName());
                edit.putString("address", device.getAddress());
                edit.commit();

                //Log.e("Preferences",preferences.getAll().toString());
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = preferences.edit();
                edit.clear();
                edit.commit();

                //Log.e("Preferences",preferences.getAll().toString());
            }
        }
    };

    @Override
    public void onDestroy(){
        unregisterReceiver(mReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        onHandleIntent();
        return null;
    }

    public int onStartCommand(Intent i, int flags, int start){
        onHandleIntent();
        return START_STICKY;
    }

}