package it.familiyparking.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CarGroupRelationTable {

    public static final String CAR_ID = "CAR_ID";
    public static final String GROUP_ID = "GROUP_ID";
	public static final String[] COLUMNS = new String[]{CAR_ID,GROUP_ID};

    public static final String TABLE = "cargroup_table";

    public static void insertRelation(SQLiteDatabase db, String carID, String groupID){

        ContentValues v = new ContentValues();
        v.put(CAR_ID,carID);
        v.put(GROUP_ID,groupID);

        db.insert(TABLE, null, v);
    }

    public static ArrayList<String> getCarID(SQLiteDatabase db,String groupID) throws SQLException{
        ArrayList<String> cars = new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, GROUP_ID+" = ? ", new String[]{groupID}, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            if(c.moveToNext())
                cars.add(c.getString(0));
        }

        c.close();

        return cars;
    }

    public static ArrayList<String> getGroupID(SQLiteDatabase db,String carID) throws SQLException{
        ArrayList<String> groups = new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, CAR_ID+" = ? ", new String[]{carID}, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            if(c.moveToNext())
                groups.add(c.getString(0));
        }

        c.close();

        return groups;
    }

    public static boolean isInRelation(SQLiteDatabase db,String carID,String groupID) throws SQLException{
        Cursor c = db.query(true, TABLE, COLUMNS, CAR_ID+" = ? AND "+GROUP_ID+" = ? ", new String[]{carID,groupID}, null, null, null, null);

        return((c != null) && (c.getCount() > 0));
    }

    public static boolean deleteCar(SQLiteDatabase db,String carID){
        return db.delete(TABLE, CAR_ID+" = ? ", new String[] {carID}) > 0;
    }

    public static boolean deleteGroup(SQLiteDatabase db,String groupID){
        return db.delete(TABLE, GROUP_ID+" = ? ", new String[] {groupID}) > 0;
    }

    public static boolean deleteCarGroupRelationTable(SQLiteDatabase db){
        return db.delete(TABLE, null, null) > 0;
    }

    public static int updateCarID(SQLiteDatabase db, String newCarID, String groupID){
        ContentValues values = new ContentValues();
        values.put(CAR_ID, newCarID);

        return db.update(TABLE, values, GROUP_ID + " = ?", new String[] { groupID });
    }

    public static int updateGroupID(SQLiteDatabase db, String newGroupID, String carID){
        ContentValues values = new ContentValues();
        values.put(GROUP_ID, newGroupID);

        return db.update(TABLE, values, CAR_ID + " = ?", new String[] { carID });
    }

}
