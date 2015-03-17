package it.familiyparking.app.parky;

import android.support.v4.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

import it.familiyparking.app.R;

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
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
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
}