package it.allindustries.bluethoottest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button =(Button)findViewById(R.id.button);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
      //  mBluetoothAdapter.
        List<String> s = new ArrayList<String>();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


    Button button ;
    public void startService()
    {
        startService(new Intent(this,ServiceBL.class));
    }

    public void stopService()
    {
        stopService(new Intent(this,ServiceBL.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService();
                return;
                /***BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                List<String> s = new ArrayList<String>();
                int connessi = 0;
                for(BluetoothDevice bt : pairedDevices) {
                    s.add(bt.getName());
                    bt.getAddress();
                    System.out.println(mBluetoothAdapter.getRemoteDevice(bt.getAddress()));
                   // mBluetoothAdapter.
                    if(bt.getBondState()==BluetoothDevice.BOND_BONDED)connessi++;
                    System.out.println(bt.getName()+"000"+bt.getBondState()+"000"+bt.getUuids());
                }
                button.setText("Connessi "+connessi);
*/
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
