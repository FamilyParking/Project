package it.familiyparking.app.listener;

import android.widget.Toast;

import it.familiyparking.app.FPApplication;
import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 05/03/15.
 */
public class NewTabListener implements android.support.v7.app.ActionBar.TabListener {

    MainActivity activity;
    String type;
    String tab_map;
    String tab_car;

    public NewTabListener(MainActivity activity, String type) {
        this.activity = activity;
        this.tab_map = activity.getResources().getString(R.string.tab_map);
        this.tab_car = activity.getResources().getString(R.string.tab_car);
        this.type = type;
    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        if(activity.getLunchWithEmptyList() && !(((FPApplication) activity.getApplication()).isGetAllCar_Running())){
            activity.setCreateCar();
            Tools.createToast(activity, activity.getResources().getString(R.string.empty_car), Toast.LENGTH_LONG);
        }
        else{
            if (type.equals(tab_car)) {
                activity.setCar();
            } else if (type.equals(tab_map)) {
                activity.setMap();
            }
        }
    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        if(type.equals(tab_car))
            activity.resetCar();
    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        if(type.equals(tab_car))
            activity.carAlreadyPressed();
    }
}
