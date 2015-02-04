package it.familiyparking.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;

import it.familiyparking.app.serverClass.Contact;

public class GroupTable {
	
	public static final String ID = "ID";
    public static final String GROUP_ID = "GROUP_ID";
    public static final String GROUP_NAME = "GROUP_NAME";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String EMAIL = "EMAIL";
    public static final String HAS_PHOTO = "HAS_PHOTO";
    public static final String PHOTO_ID = "PHOTO_ID";
	public static final String[] COLUMNS = new String[]{ID,GROUP_ID,GROUP_NAME,CONTACT_NAME,EMAIL,HAS_PHOTO,PHOTO_ID};

    public static final String TABLE = "group_table";

    public static void insertContact(SQLiteDatabase db,String groupID, String group_name, Contact contact){
        String[] data = contact.getArray();

        ContentValues v = new ContentValues();
        for(int i=0;i<COLUMNS.length;i++){
            switch (i){
                case 1 : v.put(COLUMNS[i], groupID); break;
                case 2 : v.put(COLUMNS[i], group_name); break;
                default: v.put(COLUMNS[i], data[i]);
            }
        }

        db.insert(TABLE, null, v);
    }

    public static ArrayList<Contact> getGroup(SQLiteDatabase db,String groupID, String group_name) throws SQLException{
        ArrayList<Contact> list = new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, GROUP_ID+" = ? "+GROUP_NAME+" = ?", new String[]{groupID,group_name}, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){

            while(c.moveToNext()){
                Contact contact = new Contact(c.getInt(0),c.getString(1),c.getString(2),c.getInt(3),c.getInt(4));
                list.add(contact);
            }
        }

        c.close();

        return list;
    }

    public static String[] getEmailGroup(SQLiteDatabase db,String groupID, String group_name) throws SQLException{
        Cursor c = db.query(true, TABLE, COLUMNS, GROUP_ID+" = ? "+GROUP_NAME+" = ?", new String[]{groupID,group_name}, null, null, null, null);

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

    public static boolean deleteGroup(SQLiteDatabase db,String groupID,String group_name){
        return db.delete(TABLE, GROUP_ID+" = ? "+GROUP_NAME+" = ?", new String[] {groupID,group_name}) > 0;
    }

    public static boolean deleteContact(SQLiteDatabase db,String email,String groupID,String group_name){
        return db.delete(TABLE, EMAIL + " = ? AND "+GROUP_ID+" = ? "+GROUP_NAME+" = ?", new String[] {email,groupID,group_name}) > 0;
    }

    public static boolean deleteGroupTable(SQLiteDatabase db){
        return db.delete(TABLE, null, null) > 0;
    }

}
