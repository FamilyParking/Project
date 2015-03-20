package it.familiyparking.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import it.familiyparking.app.parky.Notified;

public class NotifiedTable {

    public static final String ID = "ID";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String TIMESTAMP = "TIMESTAMP";
	public static final String[] COLUMNS = new String[]{ID,LATITUDE,LONGITUDE,TIMESTAMP};

    public static final String TABLE = "notified_table";

    public static void insertNotified(SQLiteDatabase db, Notified notified){
        String[] value = notified.toArray();

        ContentValues v = new ContentValues();
        for(int i=0; i<value.length; i++)
            v.put(COLUMNS[i], value[i]);

        db.insert(TABLE, null, v);

        db.close();
    }

    public static ArrayList<Notified> getAllNotified(SQLiteDatabase db) throws SQLException{
        ArrayList<Notified> notified = new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, null, null, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            while(c.moveToNext())
                notified.add(new Notified(c.getString(0),c.getDouble(1),c.getDouble(2),c.getString(3)));
        }

        c.close();

        db.close();

        return notified;
    }

    public static Notified getNotified_ByID(SQLiteDatabase db,String notifiedID) throws SQLException{
        Cursor c = db.query(true, TABLE, COLUMNS, ID + " = ?", new String[]{ notifiedID }, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            if(c.moveToNext())
                return new Notified(c.getString(0),c.getDouble(1),c.getDouble(2),c.getString(3));
        }

        c.close();

        db.close();

        return null;
    }

    public static int deleteNotified(SQLiteDatabase db, String notifiedID){
        return db.delete(TABLE, ID + " = ?", new String[] { notifiedID });
    }

    public static boolean deleteTable(SQLiteDatabase db){
        return db.delete(TABLE, null, null) > 0;
    }
}
