package it.familiyparking.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.dialog.ContactDetailDialog;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.fragment.CarDetailFragment;
import it.familiyparking.app.fragment.CarFragment;
import it.familiyparking.app.fragment.Confirmation;
import it.familiyparking.app.fragment.EditCar;
import it.familiyparking.app.fragment.GhostMode;
import it.familiyparking.app.fragment.Map;
import it.familiyparking.app.fragment.SignIn;
import it.familiyparking.app.fragment.TabFragment;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.task.AsyncTaskGCM;
import it.familiyparking.app.task.DoGetAllCar;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.ServiceBluetooth;
import it.familiyparking.app.utility.Tools;


public class MainActivity extends ActionBarActivity {

    private User user;
    private Map map;
    private CarFragment carFragment;
    private TabFragment tabFragment;
    private GhostMode ghostMode;
    private SignIn signIn;
    private Confirmation confirmation;
    private EditCar createCar;
    private EditCar modifyCar;
    private CarDetailFragment carDetail;
    private ProgressDialogCircular progressDialogCircular;
    private ContactDetailDialog contactDetailDialog;
    private Tracker tracker;
    private boolean inflateMenu;
    private boolean doubleBackToExitPressedOnce;
    private boolean visibleActionPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService();

        inflateMenu = false;
        doubleBackToExitPressedOnce = false;
        visibleActionPosition = false;

        tracker = Tools.activeAnalytic(this);

        Tools.setActionBar(this);

        if (savedInstanceState == null) {
            setMap();

            SQLiteDatabase db = Tools.getDB_Readable(this);
            user = UserTable.getUser(db);

            if(user == null) {
                setSignIn();
            }
            else if(!UserTable.isConfirmed(db)){
                setConfirmation();
            }
            else{
                getAllCar();
                init();
            }

            db.close();
        }
    }

    private void init(){
        map.enableGraphics(false);

        visibleActionPosition = true;

        setMenu();

        new AsyncTaskGCM().execute(user,this);
    }

    /***************************************** MENU MANAGMENT *****************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(inflateMenu)
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem localizer = menu.findItem(R.id.action_position);
        if(localizer != null)
            localizer.setVisible(visibleActionPosition);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                replaceFragment(null);
                return true;

            case R.id.action_position:
                map.updatePosition();
                return true;

            case R.id.action_cars:
                if(tabFragment == null) {
                    tabFragment = new TabFragment();
                    replaceFragment(tabFragment);
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

    public void setMenu(){
        inflateMenu = true;
        this.invalidateOptionsMenu();
    }

    public void resetMenu(){
        inflateMenu = false;
        this.invalidateOptionsMenu();
    }

    public void setMyPosition(){
        visibleActionPosition = true;
    }

    public void resetMyPosition(){
        visibleActionPosition = false;
    }

    /********************************************* SERVICE ********************************************/
    private void startService(){
        final MainActivity activity = this;

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Car car = intent.getParcelableExtra("car");
                activity.park(car);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter(Code.INTENT_TAG));

        Intent serviceIntent = new Intent(this, ServiceBluetooth.class);
        this.startService(serviceIntent);
    }

    /*********************************************** DATA *********************************************/
    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }

    public void park(Car car){
        if(map != null) {
            map.parkCar(car);
        }
    }

    private void getAllCar(){
        new Thread(new DoGetAllCar(this,user)).start();
    }

    public void updateCarAdapter(ArrayList<Car> cars){
        carFragment.updateAdapter(cars);
    }

    public void resetAppDB(){
        Tools.invalidDB(this);
    }

    public String getLatitude(){
        return map.getLatitude();
    }

    public String getLongitude(){
        return map.getLongitude();
    }

    /***************************************** FRAGMENT MANAGER ***************************************/
    private void replaceFragment(Fragment avoid){
        boolean resetUpButton = true;

        if((modifyCar != null)&&(modifyCar != avoid)){
            resetModifyCar();
            resetUpButton = false;
        }
        else if((carDetail != null)&&(carDetail != avoid)){
            resetCarDetail();
            resetUpButton = false;
        }
        else if((tabFragment != null)&&(tabFragment != avoid)){
            resetTabFragment();
        }
        else if((ghostMode != null)&&(ghostMode != avoid)){
            getSupportFragmentManager().beginTransaction().remove(ghostMode).commit();
            ghostMode = null;
        }
        if(avoid != null)
            getSupportFragmentManager().beginTransaction().add(R.id.container, avoid).commit();
        else if(resetUpButton)
            Tools.resetUpButtonActionBar(this);
    }

    public void resetAppGraphic(){
        if(map != null){
            getSupportFragmentManager().beginTransaction().remove(map).commit();
            map = null;
        }
        if(modifyCar != null){
            resetModifyCar();
        }
        if(tabFragment != null){
            resetTabFragment();
        }
        if(carFragment != null){
            getSupportFragmentManager().beginTransaction().remove(carFragment).commit();
            carFragment = null;
        }
        if(carDetail != null){
            getSupportFragmentManager().beginTransaction().remove(carDetail).commit();
            carDetail = null;
        }
        if(ghostMode != null){
            getSupportFragmentManager().beginTransaction().remove(ghostMode).commit();
            ghostMode = null;
        }
        if(signIn != null){
            getSupportFragmentManager().beginTransaction().remove(signIn).commit();
            signIn = null;
        }
        if(confirmation != null){
            getSupportFragmentManager().beginTransaction().remove(confirmation).commit();
            confirmation = null;
        }
        if(createCar != null){
            getSupportFragmentManager().beginTransaction().remove(createCar).commit();
            createCar = null;
        }
        if(modifyCar != null){
            getSupportFragmentManager().beginTransaction().remove(modifyCar).commit();
            modifyCar = null;
        }
        if(contactDetailDialog != null){
            getSupportFragmentManager().beginTransaction().remove(contactDetailDialog).commit();
            contactDetailDialog = null;
        }
        if(progressDialogCircular != null){
            getSupportFragmentManager().beginTransaction().remove(progressDialogCircular).commit();
            progressDialogCircular = null;
        }

        setMap();
        setSignIn();
    }

    /******************************************** BACK PRESS ******************************************/
    @Override
    public void onBackPressed() {
        if(progressDialogCircular != null){
            //Do nop
        }
        else if(modifyCar != null){
            resetModifyCar();
        }
        else if(carDetail != null){
            resetCarDetail();
        }
        else if(tabFragment != null){
            resetTabFragment();
        }
        else if(contactDetailDialog != null){
            resetContactDetailDialog();
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

    /****************************************** MANAGE BUTTON ****************************************/
    public void onClick_Parking(View v) {

    }

    public void setPbutton(){
        map.setPbutton();
    }

    /********************************************* FRAGMENT *******************************************/
    private void setSignIn(){
        signIn = new SignIn();
        getSupportFragmentManager().beginTransaction().add(R.id.container, signIn).commit();
    }

    public void setCarDetail(Car car){
        carDetail = new CarDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("user",user);
        bundle.putParcelable("car",car);
        carDetail.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.container, carDetail).commit();
    }

    public void setCar(){
        carFragment = new CarFragment();

        SQLiteDatabase db = Tools.getDB_Readable(this);
        ArrayList<Car> cars = CarTable.getAllCar(db);
        db.close();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("cars",cars);
        bundle.putParcelable("user",user);
        carFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_tab, carFragment).commit();
    }

    public void resetCar(){
        getSupportFragmentManager().beginTransaction().remove(carFragment).commit();
        carFragment = null;
    }

    public void setCreateCar(){
        resetModifyCar();
        resetCarDetail();

        createCar = new EditCar();

        Bundle bundle = new Bundle();
        bundle.putParcelable("user",user);
        createCar.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_tab, createCar).commit();
    }

    public void resetCreateCar(){
        getSupportFragmentManager().beginTransaction().remove(createCar).commit();
        carFragment = null;
    }

    private void setMap(){
        map = new Map();
        getSupportFragmentManager().beginTransaction().add(R.id.container, map).commit();
    }

    private void setConfirmation(){
        confirmation = new Confirmation();
        getSupportFragmentManager().beginTransaction().add(R.id.container, confirmation).commit();
    }

    private void resetConfirmation(){
        if(confirmation != null) {
            getSupportFragmentManager().beginTransaction().remove(confirmation).commit();
            confirmation = null;

            getAllCar();

            new AsyncTaskGCM().execute(user, this);
        }
    }

    public void setModifyCar(Car car){
        EditCar fragment = new EditCar();

        Bundle bundle = new Bundle();
        bundle.putParcelable("user",user);
        bundle.putParcelable("car",car);
        fragment.setArguments(bundle);

        modifyCar = fragment;
        getSupportFragmentManager().beginTransaction().add(R.id.container, modifyCar).commit();
    }

    public void resetModifyCar(){
        if(modifyCar != null) {
            getSupportFragmentManager().beginTransaction().remove(modifyCar).commit();
            modifyCar = null;
        }
    }

    private void resetCarDetail(){
        if(carDetail != null){
            getSupportFragmentManager().beginTransaction().remove(carDetail).commit();
            carDetail = null;

            Tools.setTitleActionBar(this, R.string.list_car);
        }
    }

    private void resetTabFragment(){

        resetCarDetail();

        tabFragment.removeTab();

        Tools.resetTabActionBar(this);
        Tools.resetUpButtonActionBar(this);

        getSupportFragmentManager().beginTransaction().remove(tabFragment).commit();
        tabFragment = null;
    }

    public void carAlreadyPressed(){
        if(modifyCar != null) {
            resetModifyCar();
            resetCarDetail();
        }
        else if(carDetail != null){
            resetCarDetail();
        }
    }

    public void removeContact(User contact){
        if(modifyCar != null)
            modifyCar.removeContact(contact);
        else if(createCar != null)
            createCar.removeContact(contact);
    }

    /*********************************************** TAB *********************************************/
    public void selectCarListTab(){
        if(tabFragment != null)
            tabFragment.selectCarFragment();
    }

    public void selectCarCreateTab(){
        if(tabFragment != null)
            tabFragment.selectCreateFragment();
    }
    /***************************************** MANAGE END CALL ***************************************/
    public void endSignIn(){
        getSupportFragmentManager().beginTransaction().remove(signIn).commit();
        signIn = null;

        setConfirmation();
    }

    public void endConfirmation(){
        init();
        resetConfirmation();
    }

    public void endUpdateCar(Car car){
        if(carDetail != null) {
            carDetail.updateCar(car);
        }
        
        if(carFragment != null){
            SQLiteDatabase db = Tools.getDB_Readable(this);
            ArrayList<Car> cars = CarTable.getAllCar(db);
            db.close();
            updateCarAdapter(cars);
        }
    }
    /***************************************** CLOSE FRAGMENT *****************************************/
    public void closeCarFragment(boolean flagMessage){
        getSupportFragmentManager().beginTransaction().remove(carFragment).commit();
        carFragment = null;

        setMenu();

        if(flagMessage)
            Tools.createToast(this, getResources().getText(R.string.car_empty), Toast.LENGTH_SHORT);
    }

    public void closeModifyCar(){
        setMenu();
        Tools.resetUpButtonActionBar(this);
        getSupportFragmentManager().beginTransaction().remove(modifyCar).commit();
        modifyCar = null;
    }

    /********************************************* DIALOG *********************************************/
    public void setProgressDialogCircular(String message){
        ProgressDialogCircular fragment = new ProgressDialogCircular();
        progressDialogCircular = fragment;

        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
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

    public void setContactDetailDialog(User contact){
        contactDetailDialog = new ContactDetailDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user",user);
        bundle.putParcelable("contact",contact);

        Tools.resetUpButtonActionBar(this);
        resetMenu();

        contactDetailDialog.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, contactDetailDialog).commit();
    }

    public void resetContactDetailDialog(){
        Tools.setUpButtonActionBar(this);
        setMenu();
        getSupportFragmentManager().beginTransaction().remove(contactDetailDialog).commit();
        contactDetailDialog = null;
    }
}
