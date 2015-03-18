package it.familiyparking.app.parky;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import it.familiyparking.app.R;
import it.familiyparking.app.dao.NotifiedTable;
import it.familiyparking.app.utility.Tools;


public class StatisticActivity extends FragmentActivity{

    private Tracker tracker;
    private boolean doubleBackToExitPressedOnce = false;
    private StatisticFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = new StatisticFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();

        tracker = Tools.activeAnalytic(this);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce)
            Tools.closeApp(this);

        doubleBackToExitPressedOnce = true;
        Tools.createToast(this, "Please click BACK again to exit", Toast.LENGTH_SHORT);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void updateStatistic(){
        fragment.updateData();
    }

    public void closeDialog(){
        fragment.closeDialog();
    }

}