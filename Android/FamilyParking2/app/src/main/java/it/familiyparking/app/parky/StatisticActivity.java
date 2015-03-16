package it.familiyparking.app.parky;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import it.familiyparking.app.R;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.dialog.ContactDetailDialog;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.fragment.CarDetailFragment;
import it.familiyparking.app.fragment.CarFragment;
import it.familiyparking.app.fragment.Confirmation;
import it.familiyparking.app.fragment.EditCar;
import it.familiyparking.app.fragment.GhostMode;
import it.familiyparking.app.fragment.Map;
import it.familiyparking.app.fragment.SignIn;
import it.familiyparking.app.fragment.TabFragment;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.task.AsyncTaskGCM;
import it.familiyparking.app.task.DoGetAllCar;
import it.familiyparking.app.task.DoPark;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.Tools;


public class StatisticActivity extends Activity implements View.OnClickListener{

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        findViewById(R.id.act_statistic_rl).setOnClickListener(this);

        tracker = Tools.activeAnalytic(this);

        SQLiteDatabase db = Tools.getDB_Readable(this);
        User user = UserTable.getUser(db);
        ArrayList<Car> cars = CarTable.getAllCar(db);
        db.close();

        Tools.showAlertParking(this,cars,user,true,getIntent().getStringExtra("notification_ID"));
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}