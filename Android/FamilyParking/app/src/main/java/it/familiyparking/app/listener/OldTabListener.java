package it.familiyparking.app.listener;

import android.app.ActionBar;
import android.app.FragmentTransaction;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;

/**
 * Created by francesco on 05/03/15.
 */
public class OldTabListener implements ActionBar.TabListener {

    MainActivity activity;
    String type;
    String tab_map;
    String tab_car;

    public OldTabListener(MainActivity activity, String type) {
        this.activity = activity;
        this.tab_map = activity.getResources().getString(R.string.tab_map);
        this.tab_car = activity.getResources().getString(R.string.tab_car);
        this.type = type;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if(type.equals(tab_car)){
            activity.setCar();
        }
        else if(type.equals(tab_map)){
            activity.setMap();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if(type.equals(tab_car)){
            activity.resetCar();
        }
        /*else if(type.equals(tab_map)){
            activity.resetMap();
        }*/
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if(type.equals(tab_car))
            activity.carAlreadyPressed();
    }
}
