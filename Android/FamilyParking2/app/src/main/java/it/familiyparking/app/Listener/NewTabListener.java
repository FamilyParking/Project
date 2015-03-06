package it.familiyparking.app.Listener;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;

/**
 * Created by francesco on 05/03/15.
 */
public class NewTabListener implements android.support.v7.app.ActionBar.TabListener {

    MainActivity activity;
    String type;
    String tab_car;
    String tab_create;

    public NewTabListener(MainActivity activity, String type) {
        this.activity = activity;
        this.tab_car = activity.getResources().getString(R.string.tab_car);
        this.tab_create = activity.getResources().getString(R.string.tab_create);
        this.type = type;
    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        if(type.equals(tab_car)){
            activity.setCar();
        }
        else if(type.equals(tab_create)){
            activity.setCreateCar();
        }
    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        if(type.equals(tab_car)){
            activity.resetCar();
        }
        else if(type.equals(tab_create)){
            activity.resetCreateCar();
        }
    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        if(type.equals(tab_car))
            activity.carAlreadyPressed();
    }
}
