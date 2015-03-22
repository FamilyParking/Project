package it.familiyparking.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import it.familiyparking.app.serverClass.Car;

public class CarTable {

    public static final String CAR_ID = "CAR_ID";
    public static final String NAME = "NAME";
    public static final String BRAND = "BRAND";
    public static final String REGISTER = "REGISTER";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String IS_PARKED = "ISPARKED";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String LAST_DRIVER = "LAST_DRIVER";
    public static final String BLUETOOTH_NAME = "BLUETOOTH_NAME";
    public static final String BLUETOOTH_MAC = "BLUETOOTH_MAC";
	public static final String[] COLUMNS = new String[]{CAR_ID,NAME,BRAND,REGISTER,LATITUDE,LONGITUDE,IS_PARKED,TIMESTAMP,LAST_DRIVER,BLUETOOTH_NAME,BLUETOOTH_MAC};

    public static final String TABLE = "car_table";

    public static void insertCar(SQLiteDatabase db, Car car){
        String[] data = car.getArray();

        ContentValues v = new ContentValues();
        for(int i=0;i<data.length;i++){
            v.put(COLUMNS[i], data[i]);
        }

        db.insert(TABLE, null, v);
    }

    public static Car getCar_byID(SQLiteDatabase db,String carID) throws SQLException{
        ArrayList<Car> car = new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, CAR_ID+" = ? ", new String[]{carID}, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            if(c.moveToNext())
                return new Car(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), Boolean.parseBoolean(c.getString(6)), c.getString(7), c.getString(9), c.getString(10));
        }

        c.close();

        return null;
    }

    public static ArrayList<Car> getAllCar(SQLiteDatabase db) throws SQLException{
        ArrayList<Car> car_list = new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, null, null, null, null, NAME, null);

        if((c != null) && (c.getCount() > 0)){
            while(c.moveToNext()) {
                Car car = new Car(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), Boolean.parseBoolean(c.getString(6)), c.getString(7), c.getString(9), c.getString(10));

                String last_driver = c.getString(8);
                if(last_driver != null)
                    car.setLast_driver(last_driver);

                car.setUsers(GroupTable.getContact_ByCarID(db,car.getId()));

                car_list.add(car);
            }
        }

        c.close();

        return car_list;
    }

    public static ArrayList<Car> getAllCarForBluetoothMAC(SQLiteDatabase db,String bluetoothMAC) throws SQLException{
        ArrayList<Car> car = new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, BLUETOOTH_MAC+" = ? ", new String[]{bluetoothMAC}, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            while(c.moveToNext())
                car.add(new Car(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), Boolean.parseBoolean(c.getString(6)), c.getString(7), c.getString(9), c.getString(10)));
        }

        c.close();

        return car;
    }

    public static boolean deleteCar(SQLiteDatabase db,String carID){
        return db.delete(TABLE, CAR_ID+" = ? ", new String[] {carID}) > 0;
    }

    public static boolean deleteCarTable(SQLiteDatabase db){
        return db.delete(TABLE, null, null) > 0;
    }

    public static int updateCar(SQLiteDatabase db, Car car){
        String[] data = car.getArray();

        ContentValues v = new ContentValues();
        for(int i=0;i<data.length;i++){
            v.put(COLUMNS[i], data[i]);
        }

        return db.update(TABLE, v, CAR_ID + " = ?", new String[] { car.getId() });
    }

    public static int updateBluetooth(SQLiteDatabase db, Car car){
        ContentValues values = new ContentValues();
        values.put(BLUETOOTH_NAME, car.getBluetoothName());
        values.put(BLUETOOTH_MAC, car.getBluetoothMac());

        return db.update(TABLE, values, CAR_ID + " = ?", new String[] { car.getId() });
    }

}
