package it.familiyparking.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.fragment.Create;
import it.familiyparking.app.fragment.Car;
import it.familiyparking.app.fragment.CreateGroup;
import it.familiyparking.app.fragment.GhostMode;
import it.familiyparking.app.fragment.Group;
import it.familiyparking.app.fragment.Map;
import it.familiyparking.app.fragment.SignIn;
import it.familiyparking.app.task.DoSignIn;
import it.familiyparking.app.utility.Tools;


public class MainActivity extends ActionBarActivity {

    private Map map;
    private Group group;
    private Car car;
    private GhostMode ghostMode;
    private Create create;
    private SignIn signIn;
    private CreateGroup createGroup;
    private ProgressDialogCircular progressDialogCircular;
    private Tracker tracker;
    private boolean counterclockwise;
    private boolean inflateMenu;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counterclockwise = false;
        inflateMenu = false;
        doubleBackToExitPressedOnce = false;

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
        createGroup = new CreateGroup();
        getSupportFragmentManager().beginTransaction().add(R.id.container, createGroup).commit();
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
        Tools.closeKeyboard(v,this);

        v.setVisibility(View.GONE);
        v.getRootView().findViewById(R.id.progress_signIn).setVisibility(View.VISIBLE);
        EditText email_et = (EditText) v.getRootView().findViewById(R.id.email_et);
        email_et.setKeyListener(null);
        EditText name_et = (EditText) v.getRootView().findViewById(R.id.name_surname_et);
        name_et.setKeyListener(null);

        new Thread(new DoSignIn(this,name_et.getText().toString(),email_et.getText().toString())).start();
    }

    public void callToEndSignIn(){
        setMenu();

        map.enableGraphics();

        getSupportFragmentManager().beginTransaction().remove(signIn).commit();
        signIn = null;
    }

    public void setMenu(){
        inflateMenu = true;
        this.invalidateOptionsMenu();
    }

    public void resetMenu(){
        inflateMenu = false;
        this.invalidateOptionsMenu();
    }

    public void closeCreateGroup(){
        setMenu();
        getSupportFragmentManager().beginTransaction().remove(createGroup).commit();
        createGroup = null;
    }

    public void setProgressDialogCircular(ProgressDialogCircular fragment){
        progressDialogCircular = fragment;
    }

    @Override
    public void onBackPressed() {
        if(progressDialogCircular != null){
            //Do nop
        }
        else if(createGroup != null){
            getSupportFragmentManager().beginTransaction().remove(createGroup).commit();
            createGroup = null;
        }
        else if(car != null){
            getSupportFragmentManager().beginTransaction().remove(car).commit();
            car = null;
        }
        else if(group != null){
            getSupportFragmentManager().beginTransaction().remove(group).commit();
            group = null;
        }
        else if(ghostMode != null){
            getSupportFragmentManager().beginTransaction().remove(ghostMode).commit();
            ghostMode = null;
        }
        else {
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
}
