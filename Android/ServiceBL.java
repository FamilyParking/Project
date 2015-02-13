package it.allindustries.bluethoottest;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by mauropiva on 13/02/15.
 */
public class ServiceBL extends Service
{
    public ServiceBL()
    {

    }


    protected void onHandleIntent(Intent i)
    {
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            System.out.println(device.getName());
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                System.out.println("found"); //Device found
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                System.out.println("conn"); //Device is now connected
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                System.out.println("discfin"); //Done searching
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                System.out.println("disconnreq"); //Device is about to disconnect
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                System.out.println("disconn"); //Device has disconnected
            }
        }
    };
    @Override
    public void onDestroy()
    {
        unregisterReceiver(mReceiver);
        System.out.println("uscita");
    }

    @Override
    public IBinder onBind(Intent intent) {
       onHandleIntent(intent);
        return null;
    }

    public int onStartCommand(Intent i, int flags, int start){

        onHandleIntent(i);

        return START_STICKY;
    }



}