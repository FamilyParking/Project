package com.familyparking.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.familyparking.app.serverClass.Contact;

import java.util.ArrayList;
import java.util.Arrays;

public class GroupTable {
	
	public static final String ID = "ID";
    public static final String EMAIL = "EMAIL";
    public static final String HAS_PHOTO = "HAS_PHOTO";
    public static final String PHOTO_ID = "PHOTO_ID";
	public static final String[] COLUMNS = new String[]{ID,EMAIL,HAS_PHOTO,PHOTO_ID};

    public static final String TABLE = "group_table";

    public static void insertContact(SQLiteDatabase db,Contact contact){
        Log.e("GroupTable[insertContact]",contact.toString());

        String[] data = contact.getArray();

        ContentValues v = new ContentValues();
        for(int i=0;i<COLUMNS.length;i++){
            v.put(COLUMNS[i], data[i]);
        }

        db.insert(TABLE, null, v);
    }

    public static ArrayList<Contact> getGroup(SQLiteDatabase db) throws SQLException{
        ArrayList<Contact> list = new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, null, null, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){

            //Log.e("GroupTable[getGroup]",DatabaseUtils.dumpCursorToString(c));

            while(c.moveToNext()){
                Contact contact = new Contact(c.getInt(0),c.getInt(3),c.getString(1),c.getInt(2));
                list.add(contact);
            }
        }

        c.close();

        return list;
    }

    public static String[] getEmailGroup(SQLiteDatabase db) throws SQLException{
        Cursor c = db.query(true, TABLE, new String[]{EMAIL}, null, null, null, null, null, null);

        String[] list = null;

        if((c != null) && (c.getCount() > 0)){

            //Log.e("GroupTable[getEmailGroup]", DatabaseUtils.dumpCursorToString(c));

            list = new String[c.getCount()];
            int i = 0;

            while(c.moveToNext()){
                list[i] = c.getString(0);
                i++;
            }
        }

        c.close();

        return list;
    }

    public static boolean deleteContact(SQLiteDatabase db,String id){
        return db.delete(TABLE, ID + " = ?", new String[] { id }) > 0;
    }

    public static boolean deleteGroupTable(SQLiteDatabase db){
        return db.delete(TABLE, null, null) > 0;
    }

}
