package it.familiyparking.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import it.familiyparking.app.serverClass.User;

public class GroupTable {

	public static final String CAR_ID = "CAR_ID";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String EMAIL = "EMAIL";
    public static final String HAS_PHOTO = "HAS_PHOTO";
    public static final String PHOTO_ID = "PHOTO_ID";
	public static final String[] COLUMNS = new String[]{CAR_ID,CONTACT_NAME,EMAIL,HAS_PHOTO,PHOTO_ID};

    public static final String TABLE = "group_table";

    public static void insertContact(SQLiteDatabase db, String id_car, User contact){
        String[] data = contact.getContactArray();

        ContentValues v = new ContentValues();

        v.put(CAR_ID,id_car);

        for(int i=0;i<data.length;i++)
            v.put(COLUMNS[i+1], data[i]);

        db.insert(TABLE, null, v);
    }

    public static User getContact_ByEmail(SQLiteDatabase db, String Email) throws SQLException{
        Cursor c = db.query(true, TABLE, COLUMNS, EMAIL+" == ?",new String[]{Email}, null, null, null, null);

        User user = null;

        if((c != null) && (c.getCount() > 0))
            while(c.moveToNext())
                user = new User(c.getString(1),c.getString(2),Boolean.parseBoolean(c.getString(3)),c.getString(4));

        c.close();

        return user;
    }

    public static ArrayList<User> getContact_ByCarID(SQLiteDatabase db, String carID) throws SQLException{
        Cursor c = db.query(true, TABLE, COLUMNS, CAR_ID+" == ?",new String[]{carID}, null, null, null, null);

        ArrayList<User> userArrayList = new ArrayList<>();

        if((c != null) && (c.getCount() > 0))
            while(c.moveToNext())
                userArrayList.add(new User(c.getString(1),c.getString(2),Boolean.parseBoolean(c.getString(3)),c.getString(4)));

        c.close();

        return userArrayList;
    }

    public static boolean deleteGroup(SQLiteDatabase db,String carID){
        return db.delete(TABLE, CAR_ID+" = ? ", new String[] {carID}) > 0;
    }

    public static boolean deleteContact(SQLiteDatabase db,String email,String carID){
        return db.delete(TABLE, EMAIL + " = ? AND "+CAR_ID+ " = ? ", new String[] {email,carID}) > 0;
    }

    public static boolean deleteGroupTable(SQLiteDatabase db){
        return db.delete(TABLE, null, null) > 0;
    }

}
