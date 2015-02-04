package it.familiyparking.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.analytics.Tracker;

import it.familiyparking.app.fragment.Create;
import it.familiyparking.app.fragment.Car;
import it.familiyparking.app.fragment.GhostMode;
import it.familiyparking.app.fragment.Group;
import it.familiyparking.app.fragment.Map;
import it.familiyparking.app.fragment.SignIn;
import it.familiyparking.app.utility.Tools;


public class MainActivity extends ActionBarActivity {

    private Map map;
    private Group group;
    private Car car;
    private GhostMode ghostMode;
    private Create create;
    private SignIn signIn;
    private Tracker tracker;
    private boolean counterclockwise = false;
    private boolean inflateMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tracker = Tools.activeAnalytic(this);

        Tools.setActionBar(this);

        if (savedInstanceState == null) {
            map = new Map();
            getSupportFragmentManager().beginTransaction().add(R.id.container, map).commit();

            signIn = new SignIn();
            getSupportFragmentManager().beginTransaction().add(R.id.container, signIn).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(inflateMenu)
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                replaceFragment(null);
                return true;

            case R.id.action_cars:
                car = new Car();
                replaceFragment(car);
                return true;

            case R.id.action_groups:
                group = new Group();
                replaceFragment(group);
                return true;

            case R.id.action_ghostmode:
                ghostMode = new GhostMode();
                replaceFragment(ghostMode);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void replaceFragment(Fragment avoid){
        if((car != null)&&(car != avoid)){
            getSupportFragmentManager().beginTransaction().remove(car).commit();
            car = null;
        }
        if((group != null)&&(group != avoid)){
            getSupportFragmentManager().beginTransaction().remove(group).commit();
            group = null;
        }
        if((ghostMode != null)&&(ghostMode != avoid)){
            getSupportFragmentManager().beginTransaction().remove(ghostMode).commit();
            ghostMode = null;
        }
        if(avoid != null)
            getSupportFragmentManager().beginTransaction().add(R.id.container, avoid).commit();
        else
            Tools.resetUpButtonActionBar(this);
    }

    public void onClick_Plus(View v) {
        managePlusButton();
    }

    public void onClick_Parking(View v) {
    }

    public void onClick_NewGroup(View v) {
        managePlusButton();
    }

    public void onClick_NewCar(View v) {
        managePlusButton();
    }

    private void managePlusButton(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.bottom_top, R.anim.top_bottom);

        if(counterclockwise) {
            findViewById(R.id.toCreate).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_counterclockwise));
            ft.remove(create).commit();
            create = null;
        }
        else {
            findViewById(R.id.toCreate).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise));
            create = new Create();
            ft.add(R.id.container,create).commit();
        }
        counterclockwise = !(counterclockwise);
    }

    public void onClick_SignIn(View v){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        inflateMenu = true;
        this.invalidateOptionsMenu();

        map.enableGraphics();

        getSupportFragmentManager().beginTransaction().remove(signIn).commit();
        signIn = null;
    }
}
