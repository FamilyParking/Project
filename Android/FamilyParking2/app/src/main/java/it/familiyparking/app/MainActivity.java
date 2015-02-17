package it.familiyparking.app;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.dialog.ContactDetailDialog;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.fragment.CarFragment;
import it.familiyparking.app.fragment.Create;
import it.familiyparking.app.fragment.GhostMode;
import it.familiyparking.app.fragment.GroupFragment;
import it.familiyparking.app.fragment.ManageCar;
import it.familiyparking.app.fragment.ManageGroup;
import it.familiyparking.app.fragment.Map;
import it.familiyparking.app.fragment.SignIn;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.task.DoSignIn;
import it.familiyparking.app.utility.Tools;


public class MainActivity extends ActionBarActivity {

    private Map map;
    private GroupFragment groupFragment;
    private CarFragment carFragment;
    private GhostMode ghostMode;
    private Create create;
    private SignIn signIn;
    private ManageGroup createGroup;
    private ManageGroup modifyGroup;
    private ManageCar createCar;
    private ManageCar modifyCar;
    private ProgressDialogCircular progressDialogCircular;
    private ContactDetailDialog contactDetailDialog;
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

        removeCreate(null);

        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                replaceFragment(null);
                return true;

            case R.id.action_cars:
                if(carFragment == null) {
                    DataBaseHelper databaseHelper = new DataBaseHelper(this);
                    final SQLiteDatabase db = databaseHelper.getReadableDatabase();
                    ArrayList<Car> cars = CarTable.getAllCar(db);
                    db.close();

                    if (cars.isEmpty()) {
                        Tools.createToast(this, getResources().getText(R.string.car_empty), Toast.LENGTH_SHORT);
                    } else {
                        carFragment = new CarFragment();
                        replaceFragment(carFragment);

                        return true;
                    }
                }
                return false;

            case R.id.action_groups:
                if(groupFragment == null) {
                    DataBaseHelper databaseHelper = new DataBaseHelper(this);
                    final SQLiteDatabase db = databaseHelper.getReadableDatabase();
                    ArrayList<String> list_groupID = GroupTable.getAllGroup(db);
                    db.close();

                    if (list_groupID.isEmpty()) {
                        Tools.createToast(this, getResources().getText(R.string.group_empty), Toast.LENGTH_SHORT);
                    } else {
                        groupFragment = new GroupFragment();
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("groupsID", list_groupID);
                        groupFragment.setArguments(bundle);

                        replaceFragment(groupFragment);
                    }

                    return true;
                }
                return false;

            case R.id.action_ghostmode:
                if(ghostMode == null) {
                    ghostMode = new GhostMode();
                    replaceFragment(ghostMode);
                    return true;
                }
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void replaceFragment(Fragment avoid){
        if((carFragment != null)&&(carFragment != avoid)){
            getSupportFragmentManager().beginTransaction().remove(carFragment).commit();
            carFragment = null;
        }
        if((groupFragment != null)&&(groupFragment != avoid)){
            removeGroupFragment(false);
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
        createGroup = new ManageGroup();
        getSupportFragmentManager().beginTransaction().add(R.id.container, createGroup).commit();
    }

    public void onClick_NewCar(View v) {
        setNewCar(null);
    }

    public void setNewCar(String groupID){
        createCar = new ManageCar();

        if(groupID != null){
            Bundle bundle = new Bundle();
            bundle.putString("groupID",groupID);
            createCar.setArguments(bundle);
        }
        else{
            managePlusButton();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.container, createCar).commit();
    }

    private void managePlusButton(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.bottom_top, R.anim.top_bottom);

        if(counterclockwise) {
            findViewById(R.id.toCreate).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_counterclockwise));
            removeCreate(fragmentTransaction);
        }
        else {
            findViewById(R.id.toCreate).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise));
            create = new Create();
            fragmentTransaction.add(R.id.container, create).commit();
            counterclockwise = !(counterclockwise);
        }

    }

    private void removeCreate(FragmentTransaction fragmentTransaction){
        if(create != null) {

            if(fragmentTransaction != null)
                fragmentTransaction.remove(create).commit();
            else
                getSupportFragmentManager().beginTransaction().remove(create).commit();

            create = null;

            counterclockwise = !counterclockwise;
        }
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

    public void closeCreateCar(){
        setMenu();
        Tools.resetUpButtonActionBar(this);
        getSupportFragmentManager().beginTransaction().remove(createCar).commit();
        createCar = null;
    }

    public void closeModifyGroup(){
        setMenu();
        getSupportFragmentManager().beginTransaction().remove(modifyGroup).commit();
        modifyGroup = null;
    }

    public void closeModifyCar(){
        setMenu();
        Tools.resetUpButtonActionBar(this);
        getSupportFragmentManager().beginTransaction().remove(modifyCar).commit();
        modifyCar = null;
    }

    public void setProgressDialogCircular(ProgressDialogCircular fragment){
        progressDialogCircular = fragment;
    }

    public void resetProgressDialogCircular(final boolean resetUpButton){
        getSupportFragmentManager().beginTransaction().remove(progressDialogCircular).commit();
        progressDialogCircular = null;

        if(resetUpButton)
            Tools.resetUpButtonActionBar(this);
        else
            Tools.setUpButtonActionBar(this);

        setMenu();
    }

    public void setContactDetailDialog(ContactDetailDialog fragment){
        contactDetailDialog = fragment;
    }

    public void resetContactDetailDialog(){
        Tools.setUpButtonActionBar(this);
        setMenu();
        getSupportFragmentManager().beginTransaction().remove(contactDetailDialog).commit();
        contactDetailDialog = null;
    }

    public void setModifyGroup(ManageGroup fragment){
        resetMenu();
        modifyGroup = fragment;
    }

    public void setModifyCar(ManageCar fragment){
        resetMenu();
        modifyCar = fragment;
    }

    public void resetModifyGroup(){
        Tools.setUpButtonActionBar(this);
        setMenu();
        getSupportFragmentManager().beginTransaction().remove(modifyGroup).commit();
        modifyGroup = null;
    }

    public void resetModifyCar(){
        Tools.setUpButtonActionBar(this);
        setMenu();
        getSupportFragmentManager().beginTransaction().remove(modifyCar).commit();
        modifyCar = null;
    }

    @Override
    public void onBackPressed() {
        if(progressDialogCircular != null){
            //Do nop
        }
        else if(modifyGroup != null){
            resetModifyGroup();
        }
        else if(modifyCar != null){
            resetModifyCar();
        }
        else if(contactDetailDialog != null){
            resetContactDetailDialog();
        }
        else if(createGroup != null){
            Tools.resetUpButtonActionBar(this);
            getSupportFragmentManager().beginTransaction().remove(createGroup).commit();
            createGroup = null;
        }
        else if(createCar != null){
            Tools.resetUpButtonActionBar(this);
            getSupportFragmentManager().beginTransaction().remove(createCar).commit();
            createCar = null;
        }
        else if(carFragment != null){
            Tools.resetUpButtonActionBar(this);
            getSupportFragmentManager().beginTransaction().remove(carFragment).commit();
            carFragment = null;
        }
        else if(groupFragment != null){
            Tools.resetUpButtonActionBar(this);
            getSupportFragmentManager().beginTransaction().remove(groupFragment).commit();
            groupFragment = null;
        }
        else if(ghostMode != null){
            Tools.resetUpButtonActionBar(this);
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

    public void removeGroupFragment(boolean flagMessage){
        getSupportFragmentManager().beginTransaction().remove(groupFragment).commit();
        groupFragment = null;

        setMenu();

        if(flagMessage)
            Tools.createToast(this, getResources().getText(R.string.group_empty), Toast.LENGTH_SHORT);
    }

    public void removeCarFragment(boolean flagMessage){
        getSupportFragmentManager().beginTransaction().remove(carFragment).commit();
        carFragment = null;

        setMenu();

        if(flagMessage)
            Tools.createToast(this, getResources().getText(R.string.car_empty), Toast.LENGTH_SHORT);
    }

    public void updateCarAdapter(ArrayList<Car> cars){
        carFragment.updateAdapter(cars);
    }

    public void updateGroupAdapter(ArrayList<String> list_groupID){
        groupFragment.updateAdapter(list_groupID);
    }
}
