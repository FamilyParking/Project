package it.familiyparking.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;

public class FPApplication extends Application {

    private User user;
    private ArrayList<Car> cars = new ArrayList<>();

    private boolean firstLunch = true;

    private boolean getAllCar_Running = false;
    private GoogleApiClient googleApiClient;

    public User getUser() {
        if(user != null)
            return user;

        SQLiteDatabase db = Tools.getDB_Readable(this);
        user = UserTable.getUser(db);
        db.close();

        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public void setCars(ArrayList<Car> cars) {
        this.cars = cars;
    }

    public boolean isGetAllCar_Running() {
        return getAllCar_Running;
    }

    public void setGetAllCar_Running() {
        this.getAllCar_Running = true;
    }

    public void resetGetAllCar_Running() {
        this.getAllCar_Running = false;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public boolean allHaveBluetooth(){
        if(cars.isEmpty())
            return true;

        for(Car c : cars){
            if(c.getBluetoothMac() == null)
                return false;
        }

        return true;
    }

    public boolean isFirstLunch() {
        return firstLunch;
    }

    public void updateFirstLunch() {
        this.firstLunch = false;
    }
}