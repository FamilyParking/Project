package it.familiyparking.app;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.NotifiedTable;
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
import it.familiyparking.app.parky.DoParky;
import it.familiyparking.app.parky.Notified;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.task.AsyncTaskGCM;
import it.familiyparking.app.task.DoGetAllCar;
import it.familiyparking.app.task.DoPark;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.ServerCall;
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
    private AlertDialog dialogParking;

    private BroadcastReceiver messageReceiver;
    private Tracker tracker;

    private boolean inflateMenu;
    private boolean doubleBackToExitPressedOnce;
    private boolean visibleActionPosition;
    private boolean flagAllCarRunning;
    private boolean lunchWithEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new DoParky(this)).start();

        setBroadcastReceiver();
        startService();

        initBool();

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
                init();
            }

            db.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        removeParkingNotification();
    }

    @Override
    protected void onDestroy() {
        if(messageReceiver != null){
            try {
                unregisterReceiver(messageReceiver);
            }
            catch (IllegalArgumentException e){}
        }
        super.onDestroy();
    }

    private void init(){
        map.enableGraphics(false);

        visibleActionPosition = true;

        SQLiteDatabase db = Tools.getDB_Readable(this);

        ArrayList<Car> cars = CarTable.getAllCar(db);

        db.close();

        if(cars.isEmpty())
            lunchWithEmptyList = true;

        setMenu();

        getAllCar(false);

        new AsyncTaskGCM().execute(user,this);
    }

    private void initBool(){
        inflateMenu = false;
        doubleBackToExitPressedOnce = false;
        visibleActionPosition = false;
        flagAllCarRunning = false;
        lunchWithEmptyList = false;
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
        if(localizer != null) {
            localizer.setVisible(visibleActionPosition);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(lunchWithEmptyList){
            Tools.createToast(this,"You need to create a car to use the application",Toast.LENGTH_LONG);
            return true;
        }

        switch (id){
            case android.R.id.home:
                replaceFragment(null);
                return true;

            case R.id.action_position:
                map.updatePosition();
                return true;

            case R.id.action_cars:
                setTabFragment();
                return true;

            case R.id.action_ghostmode:
                setGhostMode();
                return true;

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

    public void showMyPosition(){
        visibleActionPosition = true;
        setMenu();
    }

    public void hideMyPosition(){
        visibleActionPosition = false;
        setMenu();
    }

    /********************************************* SERVICE ********************************************/
    private void startService(){
        Tools.startService(this);
    }

    private void setBroadcastReceiver(){
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                final String action = intent.getAction();
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();

                            try {
                                Thread.sleep(1000);
                            }
                            catch (Exception e){}

                            SQLiteDatabase db = Tools.getDB_Readable(context);
                            ArrayList<Car> carID = CarTable.getAllCarForBluetoothMAC(db, device.getAddress());
                            db.close();

                            if(!user.isGhostmode()) {
                                for(final Car c : carID) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            park(c, true);
                                        }
                                    });
                                }
                            }
                        }
                    }).start();

                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,new IntentFilter(Code.ACTION_BLUETOOTH));
    }

    private void removeParkingNotification(){
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(0);
    }

    /*********************************************** DATA *********************************************/
    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }

    public void park(ArrayList<Car> cars){
        if(map != null) {
            map.parkCar(cars);
        }
    }

    public void unPark(){
        SQLiteDatabase db = Tools.getDB_Readable(this);
        ArrayList<Car> cars = CarTable.getAllCar(db);
        db.close();

        if(map != null){
            map.parkCar(cars);
        }
    }

    public void park(Car car, boolean moveCamera){
        if(map != null) {
            map.parkCar(car,moveCamera);
        }
    }

    public void moveCamera(LatLng position){
        if(map != null){
            map.moveCamera(position);
        }
    }

    public void updateCarAdapter(ArrayList<Car> cars){
        if(cars.isEmpty()) {
            setLunchWithEmptyList();
            setTabFragment();
            selectCreateCarTab();
        }
        else {
            resetLunchWithEmptyList();
        }

        if(carFragment != null)
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

    private void getAllCar(boolean background){
        if(getLunchWithEmptyList() && !background) {
            setProgressDialogCircular(getResources().getString(R.string.update_car_list));
        }

        new Thread(new DoGetAllCar(this,user,background)).start();
    }

    public void setAllCarRunning(){
        flagAllCarRunning = true;
    }

    public void resetAllCarRunning(){
        flagAllCarRunning = false;
    }

    public boolean getAllCarRunning(){
        return this.flagAllCarRunning;
    }

    public boolean getLunchWithEmptyList(){
        return this.lunchWithEmptyList;
    }

    public void resetLunchWithEmptyList(){
        this.lunchWithEmptyList = false;
    }

    public void setLunchWithEmptyList(){
        this.lunchWithEmptyList = true;
    }

    private void setGhostmodeLable(){
        if(map != null){
            map.updateGhostmodeLable();
        }
    }

    /***************************************** FRAGMENT MANAGER ***************************************/
    private void replaceFragment(Fragment avoid){
        boolean resetUpButton = true;

        if((modifyCar != null)&&(modifyCar != avoid)){
            resetModifyCar(false);
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
            resetGhostmode();
            resetUpButton = false;
        }
        if(avoid != null)
            getSupportFragmentManager().beginTransaction().add(R.id.container, avoid).commit();
        else if(resetUpButton)
            Tools.resetUpButtonActionBar(this);
    }

    public void resetAppGraphic(){
        if(signIn != null){
            getSupportFragmentManager().beginTransaction().remove(signIn).commit();
            signIn = null;
        }
        if(map != null){
            getSupportFragmentManager().beginTransaction().remove(map).commit();
            map = null;
        }
        if(confirmation != null){
            getSupportFragmentManager().beginTransaction().remove(confirmation).commit();
            confirmation = null;
        }
        if(modifyCar != null){
            resetModifyCar(false);
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
        if(modifyCar != null){
            getSupportFragmentManager().beginTransaction().remove(modifyCar).commit();
            modifyCar = null;
        }
        if(ghostMode != null){
            getSupportFragmentManager().beginTransaction().remove(ghostMode).commit();
            ghostMode = null;
        }
        if(confirmation != null){
            getSupportFragmentManager().beginTransaction().remove(confirmation).commit();
            confirmation = null;
        }
        if(createCar != null){
            getSupportFragmentManager().beginTransaction().remove(createCar).commit();
            createCar = null;
        }
        if(contactDetailDialog != null){
            getSupportFragmentManager().beginTransaction().remove(contactDetailDialog).commit();
            contactDetailDialog = null;
        }
        if(progressDialogCircular != null){
            getSupportFragmentManager().beginTransaction().remove(progressDialogCircular).commit();
            progressDialogCircular = null;
        }
        if(dialogParking != null){
            dialogParking.dismiss();
            dialogParking = null;
        }

        resetMenu();
        initBool();
        setMap();
        setSignIn();
    }

    /******************************************** BACK PRESS ******************************************/
    @Override
    public void onBackPressed() {
        if((progressDialogCircular != null) && !lunchWithEmptyList){
            //Do nop
        }
        else if((modifyCar != null) && !lunchWithEmptyList){
            resetModifyCar(false);
        }
        else if((carDetail != null) && !lunchWithEmptyList){
            resetCarDetail();
        }
        else if(((tabFragment != null) && (!lunchWithEmptyList)) && !lunchWithEmptyList){
            resetTabFragment();
        }
        else if((contactDetailDialog != null) && !lunchWithEmptyList){
            resetContactDetailDialog();
        }
        else if((createCar != null) && !lunchWithEmptyList){
            Tools.resetUpButtonActionBar(this);
            getSupportFragmentManager().beginTransaction().remove(createCar).commit();
            createCar = null;
        }
        else if((carFragment != null) && !lunchWithEmptyList){
            Tools.resetUpButtonActionBar(this);
            getSupportFragmentManager().beginTransaction().remove(carFragment).commit();
            carFragment = null;
        }
        else if((ghostMode != null) && !lunchWithEmptyList){
            resetGhostmode();
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

        SQLiteDatabase db = Tools.getDB_Readable(this);
        final ArrayList<Car> cars = CarTable.getAllCar(db);
        db.close();

        if(cars.isEmpty()){
            Tools.createToast(this,"No car is available",Toast.LENGTH_LONG);
        }
        else if(cars.size() > 1){
            dialogParking = Tools.showAlertParking(this,cars,user,false,null);
        }
        else{
            new Thread(new DoPark(this,this.user,cars.get(0))).start();
        }
    }

    public void setPbutton(){
        if(map != null)
            map.setPbutton();
    }

    /********************************************* FRAGMENT *******************************************/
    private void setSignIn(){
        signIn = new SignIn();
        getSupportFragmentManager().beginTransaction().add(R.id.container, signIn).commit();
    }

    private void setConfirmation(){
        confirmation = new Confirmation();
        getSupportFragmentManager().beginTransaction().add(R.id.container, confirmation).commit();
    }

    private void setMap(){
        map = new Map();
        getSupportFragmentManager().beginTransaction().add(R.id.container, map).commit();
    }

    public void setTabFragment(){
        if(tabFragment == null) {
            hideMyPosition();

            tabFragment = new TabFragment();
            replaceFragment(tabFragment);
        }
    }

    public void setCreateCar(){
        resetModifyCar(false);
        resetCarDetail();

        createCar = new EditCar();

        Bundle bundle = new Bundle();
        bundle.putParcelable("user",user);
        createCar.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_tab, createCar).commit();
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

    public void setCarDetail(Car car){
        carDetail = new CarDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putParcelable("car",car);
        carDetail.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.container, carDetail).commit();
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

    private void setGhostMode(){
        if(ghostMode == null) {
            ghostMode = new GhostMode();
            replaceFragment(ghostMode);
        }
    }

    private void resetConfirmation(){
        if(confirmation != null) {
            getSupportFragmentManager().beginTransaction().remove(confirmation).commit();
            confirmation = null;

            new AsyncTaskGCM().execute(user, this);
        }
    }

    public void resetTabFragment(){
        if(tabFragment != null) {
            tabFragment.removeTab();

            Tools.resetTabActionBar(this);
            Tools.resetUpButtonActionBar(this);
            Tools.setTitleActionBar(this,getResources().getString(R.string.app_name));

            resetCarDetail();
            resetCar();
            resetCreateCar();

            showMyPosition();

            getSupportFragmentManager().beginTransaction().remove(tabFragment).commit();
            tabFragment = null;
        }
    }

    public void resetCar(){
        if(carFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(carFragment).commit();
            carFragment = null;
        }
    }

    public void resetCreateCar(){
        if(createCar != null) {
            EditText editTextFinder = createCar.getEditTextFinder();
            if( editTextFinder != null){
                Tools.closeKeyboard(editTextFinder,this);
            }

            getSupportFragmentManager().beginTransaction().remove(createCar).commit();
            carFragment = null;
        }
    }

    public void resetCarDetail(){
        if(carDetail != null){
            getSupportFragmentManager().beginTransaction().remove(carDetail).commit();
            carDetail = null;

            Tools.setTitleActionBar(this, R.string.list_car);
        }
    }

    public void resetModifyCar(boolean goCarList){
        if(modifyCar != null) {
            EditText editTextFinder = modifyCar.getEditTextFinder();
            if( editTextFinder != null){
                Tools.closeKeyboard(editTextFinder,this);
            }

            getSupportFragmentManager().beginTransaction().remove(modifyCar).commit();
            modifyCar = null;

            if(goCarList)
                resetCarDetail();
        }
    }

    public void carAlreadyPressed(){
        if(modifyCar != null) {
            resetModifyCar(false);
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

    public void resetGhostmode(){
        if(ghostMode != null) {
            setMenu();
            Tools.setTitleActionBar(this,getResources().getString(R.string.app_name));

            setGhostmodeLable();

            getSupportFragmentManager().beginTransaction().remove(ghostMode).commit();
            ghostMode = null;
        }
    }

    /*********************************************** TAB *********************************************/
    public void selectCarListTab(){
        if(tabFragment != null)
            tabFragment.selectCarFragment();
    }

    public void selectCreateCarTab(){
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

    public void endNoConnection(){
        resetProgressDialogCircular(true);
        Tools.createToast(this,getResources().getString(R.string.no_connection), Toast.LENGTH_LONG);
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
        if(progressDialogCircular != null) {
            getSupportFragmentManager().beginTransaction().remove(progressDialogCircular).commit();
            progressDialogCircular = null;

            if (resetUpButton)
                Tools.resetUpButtonActionBar(this);
            else
                Tools.setUpButtonActionBar(this);

            setMenu();
        }
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
        if(contactDetailDialog != null) {
            Tools.setUpButtonActionBar(this);
            setMenu();
            getSupportFragmentManager().beginTransaction().remove(contactDetailDialog).commit();
            contactDetailDialog = null;
        }
    }

    public void resetDialogParking(){
        if(dialogParking != null){
            dialogParking.dismiss();
            dialogParking = null;
        }
    }
}