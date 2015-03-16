package it.familiyparking.app.fragment;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.listener.NewTabListener;
import it.familiyparking.app.listener.OldTabListener;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class TabFragment extends Fragment{

    private MainActivity activity;
    private String tab_car;
    private String tab_create;

    private android.support.v7.app.ActionBar actionBarNew;
    private ActionBar actionBarOld;

    private ActionBar.Tab tabListOld;
    private ActionBar.Tab tabCreateOld;

    private android.support.v7.app.ActionBar.Tab tabListNew;
    private android.support.v7.app.ActionBar.Tab tabCreateNew;

    public TabFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab, container, false);

        Tools.setUpButtonActionBar((MainActivity) getActivity());

        activity = (MainActivity)getActivity();
        tab_car = activity.getResources().getString(R.string.tab_car);
        tab_create = activity.getResources().getString(R.string.tab_create);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            oldActionBar();
        else
            newActionBar();

        return rootView;
    }

    private void oldActionBar(){
        actionBarOld = activity.getActionBar();
        actionBarOld.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        tabListOld = actionBarOld.newTab().setText(tab_car);
        tabCreateOld = actionBarOld.newTab().setText(tab_create);

        tabListOld.setTabListener(new OldTabListener(activity, tab_car));
        tabCreateOld.setTabListener(new OldTabListener(activity, tab_create));

        actionBarOld.addTab(tabListOld);
        actionBarOld.addTab(tabCreateOld);
    }

    private void newActionBar(){
        actionBarNew = activity.getSupportActionBar();
        actionBarNew.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        tabListNew = actionBarNew.newTab().setText(tab_car);
        tabCreateNew = actionBarNew.newTab().setText(tab_create);

        tabListNew.setTabListener(new NewTabListener(activity, tab_car));
        tabCreateNew.setTabListener(new NewTabListener(activity, tab_create));

        actionBarNew.addTab(tabListNew);
        actionBarNew.addTab(tabCreateNew);
    }

    public void selectCarFragment(){
        if(tabListNew != null)
            tabListNew.select();
        else if(tabListOld != null)
            tabListOld.select();
    }

    public void selectCreateFragment(){
        if(tabCreateNew != null)
            tabCreateNew.select();
        else if(tabCreateOld != null)
            tabCreateOld.select();
    }

    public void removeTab(){
        if(actionBarNew != null) {
            actionBarNew.removeTab(tabListNew);
            actionBarNew.removeTab(tabCreateNew);
            actionBarNew.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
        else if(actionBarOld != null) {
            actionBarOld.removeTab(tabListOld);
            actionBarOld.removeTab(tabCreateOld);
            actionBarOld.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
    }

}

