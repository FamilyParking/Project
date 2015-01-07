package com.familyparking.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.familyparking.app.serverClass.Contact;

import java.util.ArrayList;

public class GroupTable {
	
	public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String HAS_PHOTO = "HAS_PHOTO";
    public static final String PHOTO_ID = "PHOTO_ID";
	public static final String[] COLUMNS = new String[]{ID,NAME,EMAIL,HAS_PHOTO,PHOTO_ID};

    public static final String TABLE = "group_table";

    public static void insertContact(SQLiteDatabase db,Contact contact){
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

            while(c.moveToNext()){
                Contact contact = new Contact(c.getInt(0),c.getString(1),c.getString(2),c.getInt(3),c.getInt(4));
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

    public static boolean deleteContact(SQLiteDatabase db,String email){
        return db.delete(TABLE, EMAIL + " = ?", new String[] { email }) > 0;
    }

    public static boolean deleteGroupTable(SQLiteDatabase db){
        return db.delete(TABLE, null, null) > 0;
    }

}
