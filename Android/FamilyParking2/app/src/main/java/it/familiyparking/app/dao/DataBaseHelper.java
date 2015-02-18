package it.familiyparking.app.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{

    public static final String NAME_DB = "familyparking_db";
    public static final int VERSION_DB = 1;

    public static final String CREATE_TABLE_GROUP =
            "CREATE TABLE IF NOT EXISTS "+ GroupTable.TABLE+" ( " +
                    GroupTable.ID+" TEXT NOT NULL, "+
                    GroupTable.GROUP_ID+" TEXT NOT NULL, "+
                    GroupTable.GROUP_NAME+" TEXT NOT NULL, "+
                    GroupTable.TIMESTAMP+" TEXT NOT NULL, "+
                    GroupTable.CONTACT_NAME+" TEXT NOT NULL, "+
                    GroupTable.EMAIL+" TEXT NOT NULL, "+
                    GroupTable.HAS_PHOTO+" INTEGER DEFAULT 0, "+
                    GroupTable.PHOTO_ID+" TEXT NOT NULL, "+
                    "PRIMARY KEY ( "+GroupTable.GROUP_ID+" , "+GroupTable.EMAIL+" )"+" ) ; ";

    public static final String CREATE_TABLE_CAR =
            "CREATE TABLE IF NOT EXISTS "+ CarTable.TABLE+" ( " +
                    CarTable.CAR_ID+" TEXT NOT NULL, "+
                    CarTable.NAME+" TEXT NOT NULL, "+
                    CarTable.BRAND+" TEXT NOT NULL, "+
                    CarTable.BLUETOOTH_NAME+" TEXT , "+
                    CarTable.BLUETOOTH_MAC+" TEXT , "+
                    CarTable.TIMESTAMP+" TEXT NOT NULL, "+
                    "PRIMARY KEY ( "+CarTable.CAR_ID+" )"+" ) ; ";

    public static final String CREATE_TABLE_USER =
            "CREATE TABLE IF NOT EXISTS "+ UserTable.TABLE+" ( " +
                    UserTable.CODE+" TEXT, "+
                    UserTable.NAME+" TEXT NOT NULL, "+
                    UserTable.EMAIL+" TEXT NOT NULL, "+
                    UserTable.DEVICE_ID+" TEXT, "+
                    UserTable.HAS_PHOTO+" TEXT, "+
                    UserTable.PHOTO_ID+" TEXT, "+
                    UserTable.GHOST_MODE+" TEXT, "+
                    "PRIMARY KEY ( "+UserTable.DEVICE_ID+" )"+" ) ; ";

    public static final String CREATE_TABLE_CAR_GROUP_RELATION =
            "CREATE TABLE IF NOT EXISTS "+ CarGroupRelationTable.TABLE+" ( " +
                    CarGroupRelationTable.GROUP_ID+" TEXT NOT NULL, "+
                    CarGroupRelationTable.CAR_ID+" TEXT NOT NULL, "+
                    "PRIMARY KEY ( "+CarGroupRelationTable.GROUP_ID+" , "+CarGroupRelationTable.CAR_ID+" )"+" ) ; ";



    public static final String DROP_TABLE_GROUP = "DROP TABLE IF EXISTS "+ GroupTable.TABLE+" ;";
    public static final String DROP_TABLE_CAR = "DROP TABLE IF EXISTS "+ CarTable.TABLE+" ;";
    public static final String DROP_TABLE_CAR_GROUP_RELATION = "DROP TABLE IF EXISTS "+ CarGroupRelationTable.TABLE+" ;";
    public static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS "+ UserTable.TABLE+" ;";

    public DataBaseHelper(Context context){
        super(context, NAME_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_GROUP);
        db.execSQL(CREATE_TABLE_CAR);
        db.execSQL(CREATE_TABLE_CAR_GROUP_RELATION);
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_GROUP);
        db.execSQL(DROP_TABLE_CAR);
        db.execSQL(DROP_TABLE_CAR_GROUP_RELATION);
        db.execSQL(DROP_TABLE_USER);
        onCreate(db);
    }

}
