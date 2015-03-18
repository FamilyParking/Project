package it.familiyparking.app.parky;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 23/03/14.
 */
public class ParkyBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){

        Log.e("ParkyBroadcastReceiver",intent.getAction());

        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)){
            Tools.startService(context);
        }
        else if(intent.getAction().equalsIgnoreCase(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
            Log.e("ParkyBroadcastReceiver","Disconnected");
        }
        else if(intent.getAction().equalsIgnoreCase(BluetoothDevice.ACTION_ACL_CONNECTED)){
            Log.e("ParkyBroadcastReceiver","Connected");
        }
    }
}
