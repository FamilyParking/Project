package it.familiyparking.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;

public class UserTable {

    public static final String CODE = "CODE";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String HAS_PHOTO = "HAS_PHOTO";
    public static final String PHOTO_ID = "PHOTO_ID";
    public static final String GHOST_MODE = "GHOST_MODE";
	public static final String[] COLUMNS = new String[]{CODE,NAME,EMAIL,DEVICE_ID,HAS_PHOTO,PHOTO_ID,GHOST_MODE};

    public static final String TABLE = "user_table";

    public static void insertUser(SQLiteDatabase db, User user){
        String[] data = user.getArray();

        ContentValues v = new ContentValues();
        for(int i=0;i<data.length;i++){
            v.put(COLUMNS[i], data[i]);
        }

        db.insert(TABLE, null, v);
    }

    public static User getUser(SQLiteDatabase db) throws SQLException{
        Cursor c = db.query(true, TABLE, COLUMNS, null, null, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            if(c.moveToNext())
                return new User(c.getString(0),c.getString(1),c.getString(2),c.getString(3),Boolean.parseBoolean(c.getString(4)),c.getString(5),Boolean.parseBoolean(c.getString(6)));
        }

        c.close();

        return null;
    }

    public static boolean isConfirmed(SQLiteDatabase db){
        Cursor c = db.query(true, TABLE, new String[]{CODE}, null, null, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            if(c.moveToNext()) {
                String code = c.getString(0);
                return code != null;
            }
        }

        c.close();

        return false;
    }

    public static boolean deleteUser(SQLiteDatabase db){
        return db.delete(TABLE, null, null) > 0;
    }

    public static int updateGhostmode(SQLiteDatabase db, boolean ghostmode){
        ContentValues values = new ContentValues();
        values.put(GHOST_MODE, Boolean.toString(ghostmode));

        return db.update(TABLE, values, null, null);
    }

    public static int updateUser(SQLiteDatabase db, User user){
        String[] data = user.getArray();

        ContentValues v = new ContentValues();
        for(int i=0;i<data.length;i++){
            v.put(COLUMNS[i], data[i]);
        }

        return db.update(TABLE, v, null, null);
    }

}
