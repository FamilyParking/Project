package it.familiyparking.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import it.familiyparking.app.serverClass.Contact;

public class GroupTable {
	
	public static final String ID = "ID";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String EMAIL = "EMAIL";
    public static final String HAS_PHOTO = "HAS_PHOTO";
    public static final String PHOTO_ID = "PHOTO_ID";
    public static final String GROUP_ID = "GROUP_ID";
    public static final String GROUP_NAME = "GROUP_NAME";
    public static final String TIMESTAMP = "TIMESTAMP";
	public static final String[] COLUMNS = new String[]{ID,CONTACT_NAME,EMAIL,HAS_PHOTO,PHOTO_ID,GROUP_ID,GROUP_NAME,TIMESTAMP};

    public static final String TABLE = "group_table";

    public static void insertContact(SQLiteDatabase db,String groupID, String group_name, Contact contact, String timestamp){
        String[] data = contact.getArray();

        ContentValues v = new ContentValues();
        for(int i=0;i<COLUMNS.length;i++){
            switch (i){
                case 5 : v.put(COLUMNS[i], groupID); break;
                case 6 : v.put(COLUMNS[i], group_name); break;
                case 7 : v.put(COLUMNS[i], timestamp); break;
                default: v.put(COLUMNS[i], data[i]);
            }
        }

        db.insert(TABLE, null, v);
    }

    public static ArrayList<Contact> getGroup(SQLiteDatabase db,String groupID) throws SQLException{
        ArrayList<Contact> list = new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, GROUP_ID+" = ? ", new String[]{groupID}, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){

            while(c.moveToNext()){
                Contact contact = new Contact(c.getInt(0),c.getString(1),c.getString(2),c.getInt(3),c.getInt(4));
                list.add(contact);
            }
        }

        c.close();

        return list;
    }

    public static String getGroupIDbyName(SQLiteDatabase db, String group_name) throws SQLException{
        Cursor c = db.query(true, TABLE, new String[]{GROUP_ID}, GROUP_NAME+" = ?", new String[]{group_name}, null, null, null, "1");

        if((c != null) && (c.getCount() > 0) && (c.moveToNext()))
            return c.getString(0);

        c.close();

        return null;
    }

    public static String getGroupNamebyID(SQLiteDatabase db, String groupID) throws SQLException{
        Cursor c = db.query(true, TABLE, new String[]{GROUP_NAME}, GROUP_ID+" = ?", new String[]{groupID}, null, null, null, "1");

        if((c != null) && (c.getCount() > 0) && (c.moveToNext()))
            return c.getString(0);

        c.close();

        return null;
    }

    public static ArrayList<String> getAllGroup(SQLiteDatabase db) throws SQLException{
        ArrayList<String> list = new ArrayList<>();

        Cursor c = db.query(true, TABLE, new String[]{GROUP_ID}, null, null, null, null, null, null);

        if((c != null) && (c.getCount() > 0) && (c.moveToNext()))
            list.add(c.getString(0));

        c.close();

        return list;
    }

    public static String[] getEmailGroup(SQLiteDatabase db,String groupID) throws SQLException{
        Cursor c = db.query(true, TABLE, COLUMNS, GROUP_ID+" = ? ", new String[]{groupID}, null, null, null, null);

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

    public static boolean deleteGroup(SQLiteDatabase db,String groupID){
        return db.delete(TABLE, GROUP_ID+" = ? ", new String[] {groupID}) > 0;
    }

    public static boolean deleteContact(SQLiteDatabase db,String email,String groupID){
        return db.delete(TABLE, EMAIL + " = ? AND "+GROUP_ID+ " = ? ", new String[] {email,groupID}) > 0;
    }

    public static boolean deleteGroupTable(SQLiteDatabase db){
        return db.delete(TABLE, null, null) > 0;
    }

    public static int updateNameGroup(SQLiteDatabase db, String oldName, String newName){
        ContentValues values = new ContentValues();
        values.put(GROUP_NAME, newName);

        return db.update(TABLE, values, GROUP_NAME + " = ?", new String[] { oldName });
    }

}
