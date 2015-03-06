package it.familiyparking.app.Listener;

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
    String tab_car;
    String tab_create;

    public OldTabListener(MainActivity activity, String type) {
        this.activity = activity;
        this.tab_car = activity.getResources().getString(R.string.tab_car);
        this.tab_create = activity.getResources().getString(R.string.tab_create);
        this.type = type;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if(type.equals(tab_car)){
            activity.setCar();
        }
        else if(type.equals(tab_create)){
            activity.setCreateCar();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if(type.equals(tab_car)){
            activity.resetCar();
        }
        else if(type.equals(tab_create)){
            activity.resetCreateCar();
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if(type.equals(tab_car))
            activity.carAlreadyPressed();
    }
}
