package it.familiyparking.app.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by francesco on 23/03/14.
 */
public class FPBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){

        Log.e("FPBroadcastReceiver",intent.toString());

        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)){

            Log.e("FPBroadcastReceiver",intent.toString());

            Tools.startService(context);
        }
    }
}
