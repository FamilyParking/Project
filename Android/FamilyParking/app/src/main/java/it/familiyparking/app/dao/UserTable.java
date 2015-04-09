package it.familiyparking.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import it.familiyparking.app.serverClass.User;

public class UserTable {

    public static final String CODE = "CODE";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String GCM_ID = "GCM_ID";
    public static final String HAS_PHOTO = "HAS_PHOTO";
    public static final String PHOTO_ID = "PHOTO_ID";
    public static final String GHOST_MODE = "GHOST_MODE";
    public static final String PARKY = "PARKY";
    public static final String NOTIFICATION = "NOTIFICATION";
	public static final String[] COLUMNS = new String[]{CODE,NAME,EMAIL,GCM_ID,HAS_PHOTO,PHOTO_ID,GHOST_MODE,PARKY,NOTIFICATION};

    public static final String TABLE = "user_table";

    public static void insertUser(SQLiteDatabase db, User user){
        user.setEmail(user.getEmail().toLowerCase());

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
            if(c.moveToNext()) {
                boolean ghostmode = (c.getInt(6) == 1);
                boolean parky = (c.getInt(7) == 1);
                boolean notification = (c.getInt(8) == 1);

                return new User(c.getString(0),c.getString(1),c.getString(2), c.getString(3), Boolean.parseBoolean(c.getString(4)), c.getString(5), ghostmode, parky, notification);
            }
        }

        c.close();

        return null;
    }

    public static boolean getGhostMode(SQLiteDatabase db) throws SQLException{
        Cursor c = db.query(true, TABLE, new String[]{GHOST_MODE}, null, null, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            if(c.moveToNext())
                return c.getInt(0) == 1;
        }

        c.close();

        return false;
    }

    public static boolean getNotification(SQLiteDatabase db) throws SQLException{
        Cursor c = db.query(true, TABLE, new String[]{NOTIFICATION}, null, null, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            if(c.moveToNext())
                return c.getInt(0) == 1;
        }

        c.close();

        return false;
    }

    public static boolean getParky(SQLiteDatabase db) throws SQLException{
        Cursor c = db.query(true, TABLE, new String[]{PARKY}, null, null, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            if(c.moveToNext())
                return c.getInt(0) == 1;
        }

        c.close();

        return false;
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
        if(ghostmode)
            values.put(GHOST_MODE, 1);
        else
            values.put(GHOST_MODE, 0);

        return db.update(TABLE, values, null, null);
    }

    public static int updateParky(SQLiteDatabase db, boolean parky){
        ContentValues values = new ContentValues();
        if(parky)
            values.put(PARKY, 1);
        else
            values.put(PARKY, 0);

        return db.update(TABLE, values, null, null);
    }

    public static int updateNotification(SQLiteDatabase db, boolean notification){
        ContentValues values = new ContentValues();
        if(notification)
            values.put(NOTIFICATION, 1);
        else
            values.put(NOTIFICATION, 0);

        return db.update(TABLE, values, null, null);
    }

    public static int updateGCM_ID(SQLiteDatabase db, String deviceID){
        ContentValues values = new ContentValues();
        values.put(GCM_ID,deviceID);

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
