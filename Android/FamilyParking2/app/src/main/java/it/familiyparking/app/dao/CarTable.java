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
    public static final String BLUETOOTH_NAME = "BLUETOOTH_NAME";
    public static final String BLUETOOTH_MAC = "BLUETOOTH_MAC";
    public static final String TIMESTAMP = "TIMESTAMP";
	public static final String[] COLUMNS = new String[]{CAR_ID,NAME,BRAND,BLUETOOTH_MAC,BLUETOOTH_NAME,TIMESTAMP};

    public static final String TABLE = "car_table";

    public static void insertCar(SQLiteDatabase db, Car car, String timestamp){
        String[] data = car.getArray();

        ContentValues v = new ContentValues();
        for(int i=0;i<data.length;i++){
            v.put(COLUMNS[i], data[i]);
        }
        v.put(COLUMNS[COLUMNS.length-1], timestamp);

        db.insert(TABLE, null, v);
    }

    public static Car getCar(SQLiteDatabase db,String carID) throws SQLException{
        Car car = null;

        Cursor c = db.query(true, TABLE, COLUMNS, CAR_ID+" = ? ", new String[]{carID}, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            if(c.moveToNext())
                car = new Car(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4));
        }

        c.close();

        return car;
    }

    public static ArrayList<Car> getAllCar(SQLiteDatabase db) throws SQLException{
        ArrayList<Car> car = new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, null, null, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            while(c.moveToNext())
                car.add(new Car(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4)));
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

    public static int updateNameCar(SQLiteDatabase db, String carID, String newName){
        ContentValues values = new ContentValues();
        values.put(NAME, newName);

        return db.update(TABLE, values, CAR_ID + " = ?", new String[] { carID });
    }

    public static int updateNameBrand(SQLiteDatabase db, String carID, String newBrand){
        ContentValues values = new ContentValues();
        values.put(BRAND, newBrand);

        return db.update(TABLE, values, CAR_ID + " = ?", new String[] { carID });
    }

    public static int updateBluetooth(SQLiteDatabase db, Car car){
        ContentValues values = new ContentValues();
        values.put(BLUETOOTH_NAME, car.getBluetoothName());
        values.put(BLUETOOTH_MAC, car.getBluetoothMac());

        return db.update(TABLE, values, CAR_ID + " = ?", new String[] { car.getId() });
    }

}
