package it.familiyparking.app;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.dialog.ContactDetailDialog;
import it.familiyparking.app.dialog.ProgressDialogCircularMain;
import it.familiyparking.app.fragment.BackgroundMap;
import it.familiyparking.app.fragment.CarDetailFragment;
import it.familiyparking.app.fragment.CarFragment;
import it.familiyparking.app.fragment.Confirmation;
import it.familiyparking.app.fragment.EditCar;
import it.familiyparking.app.fragment.FixPosition;
import it.familiyparking.app.fragment.GhostMode;
import it.familiyparking.app.fragment.Map;
import it.familiyparking.app.fragment.SignIn;
import it.familiyparking.app.fragment.TabFragment;
import it.familiyparking.app.parky.StatisticActivity;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.task.AsyncTaskGCM;
import it.familiyparking.app.task.DoGetAllCar;
import it.familiyparking.app.task.DoPark;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.Tools;


public class MainActivity extends ActionBarActivity {

    private FPApplication application;

    private BackgroundMap backgroundMap;
    private TabFragment tabFragment;
    private Map map;
    private CarFragment carFragment;
    private GhostMode ghostMode;
    private SignIn signIn;
    private Confirmation confirmation;
    private EditCar createCar;
    private EditCar modifyCar;
    private CarDetailFragment carDetail;
    private FixPosition fixPosition;
    private ProgressDialogCircularMain progressDialogCircular;
    private ContactDetailDialog contactDetailDialog;
    private AlertDialog dialogParking;

    private Tracker tracker;

    private boolean inflateMenu;
    private boolean doubleBackToExitPressedOnce;
    private boolean visibleActionPosition;
    private boolean lunchWithEmptyList;

    private String car_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = (FPApplication)getApplication();

        car_id = getIntent().getStringExtra("car_id");

        startGoogleApi();

        initBool();

        tracker = Tools.activeAnalytic(this);

        Tools.setActionBar(this);

        if (savedInstanceState == null) {

            SQLiteDatabase db = Tools.getDB_Readable(this);
            application.setUser(UserTable.getUser(db));

            if(application.getUser() == null) {
                setBackgroundMap();
                setSignIn();
            }
            else if(!UserTable.isConfirmed(db)){
                setBackgroundMap();
                setConfirmation();
            }
            else{
                init();
            }

            db.close();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String carID_notification = intent.getStringExtra("car_id");
        if(carID_notification != null){
            getAllCar(false,carID_notification);
        }

        String carID_bluetooth = intent.getStringExtra("parked");
        if(carID_bluetooth != null){
            if(map != null){
                SQLiteDatabase db = Tools.getDB_Readable(this);
                map.parkCar(CarTable.getAllCar(db),carID_bluetooth);
                db.close();
            }
        }
    }

    private void init(){
        setTabFragment();

        visibleActionPosition = true;

        SQLiteDatabase db = Tools.getDB_Readable(this);

        ArrayList<Car> cars = CarTable.getAllCar(db);

        db.close();

        if(cars.isEmpty())
            lunchWithEmptyList = true;

        setMenu();

        getAllCar(false,car_id);

        resetBackgroundMap();

        new AsyncTaskGCM().execute(application.getUser(),this);
    }

    private void initBool(){
        inflateMenu = false;
        doubleBackToExitPressedOnce = false;
        visibleActionPosition = false;
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

        MenuItem ghostmode = menu.findItem(R.id.action_ghostmode);
        if(ghostmode != null) {
            SQLiteDatabase db = Tools.getDB_Readable(this);
            ghostmode.setChecked(UserTable.getGhostMode(db));
            db.close();
        }

        MenuItem parky = menu.findItem(R.id.action_parky);
        if(parky != null) {
            SQLiteDatabase db = Tools.getDB_Readable(this);
            parky.setChecked(UserTable.getParky(db));
            db.close();
        }

        MenuItem notification = menu.findItem(R.id.action_notification);
        if(notification != null) {
            SQLiteDatabase db = Tools.getDB_Readable(this);
            notification.setChecked(UserTable.getNotification(db));
            db.close();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        SQLiteDatabase db = null;
        boolean flag = false;
        if((id != android.R.id.home) && (id != R.id.action_position)){
            db = Tools.getDB_Writable(this);
            flag = item.isChecked();
        }


        switch (id){
            case android.R.id.home:
                replaceFragment();
                return true;

            case R.id.action_position:
                map.updatePosition();
                return true;

            case R.id.action_ghostmode:
                item.setChecked(!flag);
                UserTable.updateGhostmode(db,!flag);
                db.close();
                application.getUser().setGhostmode(!flag);
                setGhostmodeLable();
                return true;

            case R.id.action_parky:
                item.setChecked(!flag);
                UserTable.updateParky(db,!flag);
                db.close();

                if(!flag){
                    GoogleApiClient googleApiClient = ((FPApplication)getApplication()).getGoogleApiClient();
                    if(googleApiClient != null)
                        googleApiClient.disconnect();
                }

                return true;

            case R.id.action_notification:
                item.setChecked(!flag);
                UserTable.updateNotification(db,!flag);
                db.close();
                return true;

            case R.id.action_statistics:
                Intent activity = new Intent(getBaseContext(),StatisticActivity.class);
                activity.putExtra("from",Code.MAIN_ACTIVITY);
                startActivity(activity);
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
    private void startGoogleApi(){
        Intent i = new Intent();
        i.setAction(Code.CUSTOM_INTENT);
        sendBroadcast(i);
    }

    private void removeParkingNotification(){
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(0);
    }

    /*********************************************** DATA *********************************************/

    public void park(ArrayList<Car> cars, String car_id){
        if(map != null) {
            map.parkCar(cars,car_id);
        }
    }

    public void unPark(){
        SQLiteDatabase db = Tools.getDB_Readable(this);
        ArrayList<Car> cars = CarTable.getAllCar(db);
        db.close();

        if(map != null){
            map.parkCar(cars,null);
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
            setTabCar();
            setCreateCar();
        }
        else {
            resetLunchWithEmptyList();

            if(carFragment != null)
                carFragment.updateAdapter(cars);
        }
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

    private void getAllCar(boolean background, String car_id){
        if(getLunchWithEmptyList() && !background) {
            setProgressDialogCircular(getResources().getString(R.string.update_car_list));
        }

        new Thread(new DoGetAllCar(this,application.getUser(),background,car_id)).start();
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

    public void updateCarDetailPosition(){
        if(carDetail != null)
            carDetail.updateCarPosition();
    }

    /***************************************** FRAGMENT MANAGER ***************************************/
    private void replaceFragment(){
        if(modifyCar != null){
            resetModifyCar(false);
        }
        else if(fixPosition != null){
            resetFixPosition();
        }
        else if(createCar != null){
            resetCreateCar();
        }
        else if(carDetail != null){
            resetCarDetail();
        }
        else if(ghostMode != null){
            resetGhostmode();
        }
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
        if(tabFragment != null){
            resetTabFragment();
        }

        resetMenu();
        initBool();
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
        else if((fixPosition != null) && !lunchWithEmptyList){
            resetFixPosition();
        }
        else if((carDetail != null) && !lunchWithEmptyList){
            resetCarDetail();
        }
        else if((createCar != null) && !lunchWithEmptyList){
            resetCreateCar();
        }
        else if((contactDetailDialog != null) && !lunchWithEmptyList){
            resetContactDetailDialog();
        }
        else if((carFragment != null) && !lunchWithEmptyList){
            setTabMap();
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
            dialogParking = Tools.showAlertParking(this,cars,application.getUser(),false,null);
        }
        else{
            new Thread(new DoPark(this,this.application.getUser(),cars.get(0))).start();
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

    public boolean signInIsShown(){
        return (signIn != null);
    }

    private void setConfirmation(){
        confirmation = new Confirmation();
        getSupportFragmentManager().beginTransaction().add(R.id.container, confirmation).commit();
    }

    private void setBackgroundMap(){
        if(backgroundMap == null) {
            backgroundMap = new BackgroundMap();
            getSupportFragmentManager().beginTransaction().add(R.id.container, backgroundMap).commit();
        }
    }

    private void resetBackgroundMap(){
        if(backgroundMap != null) {
            getSupportFragmentManager().beginTransaction().remove(backgroundMap).commit();
            backgroundMap = null;
        }
    }

    private void setTabFragment(){
        tabFragment = new TabFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, tabFragment).commit();
    }

    public void setTabCar(){
        if(tabFragment != null){
            tabFragment.selectCarFragment();
        }
    }

    public void setTabMap(){
        if(tabFragment != null){
            tabFragment.selectMapFragment();
        }
    }

    private void resetTabFragment(){
        if(tabFragment != null) {
            tabFragment.removeTab();

            Tools.resetTabActionBar(this);
            Tools.resetUpButtonActionBar(this);
            Tools.setTitleActionBar(this,getResources().getString(R.string.app_name));

            resetCarDetail();
            resetCar();
            resetCreateCar();

            getSupportFragmentManager().beginTransaction().remove(tabFragment).commit();
            tabFragment = null;
        }
    }

    public void setMap(){
        if(map == null) {
            Bundle bundle = new Bundle();
            bundle.putString("car_id", car_id);

            map = new Map();
            map.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.container, map).commit();
        }
    }

    public void setCreateCar(){
        resetModifyCar(false);
        resetCarDetail();

        createCar = new EditCar();
        Bundle bundle = new Bundle();
        bundle.putParcelable("car",null);
        createCar.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, createCar).commit();
    }

    public void setCar(){
        if(carFragment == null) {
            carFragment = new CarFragment();

            hideMyPosition();

            SQLiteDatabase db = Tools.getDB_Readable(this);
            ArrayList<Car> cars = CarTable.getAllCar(db);
            db.close();

            getSupportFragmentManager().beginTransaction().add(R.id.container, carFragment).commit();
        }
    }

    public void setCarDetail(Car car){
        carDetail = new CarDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("car",car);
        carDetail.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.container, carDetail).commit();
    }

    public void setModifyCar(Car car){
        EditCar fragment = new EditCar();
        Bundle bundle = new Bundle();
        bundle.putParcelable("car",car);
        fragment.setArguments(bundle);

        modifyCar = fragment;
        getSupportFragmentManager().beginTransaction().add(R.id.container, modifyCar).commit();
    }

    private void setGhostMode(){
        if(ghostMode == null) {
            ghostMode = new GhostMode();
            resetCreateCar();
            resetModifyCar(false);
            resetFixPosition();
            resetCarDetail();
            resetCar();

            getSupportFragmentManager().beginTransaction().add(R.id.container, ghostMode).commit();
        }
    }

    private void resetConfirmation(){
        if(confirmation != null) {
            getSupportFragmentManager().beginTransaction().remove(confirmation).commit();
            confirmation = null;

            new AsyncTaskGCM().execute(application.getUser(), this);
        }
    }

    public void resetCar(){
        if(carFragment != null) {

            Tools.setTitleActionBar(this,getResources().getString(R.string.app_name));

            showMyPosition();

            resetCarDetail();
            resetCreateCar();

            getSupportFragmentManager().beginTransaction().remove(carFragment).commit();
            carFragment = null;

            setTabMap();
        }
    }

    public void resetCreateCar(){
        if(createCar != null) {
            EditText editTextFinder = createCar.getEditTextFinder();
            if( editTextFinder != null){
                Tools.closeKeyboard(editTextFinder,this);
            }

            Tools.resetUpButtonActionBar(this);

            getSupportFragmentManager().beginTransaction().remove(createCar).commit();
            createCar = null;
        }
    }

    public void resetCarDetail(){
        if(carDetail != null){
            getSupportFragmentManager().beginTransaction().remove(carDetail).commit();
            carDetail = null;

            Tools.setTitleActionBar(this, R.string.list_car);
            Tools.resetUpButtonActionBar(this);
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

    public void setFixPositionMarker(Car car) {
        fixPosition = new FixPosition();
        Bundle bundle = new Bundle();
        bundle.putParcelable("car",car);
        fixPosition.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.container, fixPosition).commit();
    }

    public void resetFixPosition(){
        if(fixPosition != null){
            getSupportFragmentManager().beginTransaction().remove(fixPosition).commit();
            fixPosition = null;
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

    public boolean setTitleNameCar(){
        return !((fixPosition != null) || (modifyCar != null) || (createCar != null));
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
        ProgressDialogCircularMain fragment = new ProgressDialogCircularMain();
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