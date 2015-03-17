package it.familiyparking.app.parky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 23/03/14.
 */
public class ParkyBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){

        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)){
            Tools.startService(context);
        }
    }
}
