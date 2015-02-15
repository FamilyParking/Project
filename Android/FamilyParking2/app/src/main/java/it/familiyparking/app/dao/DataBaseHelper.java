package it.familiyparking.app.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import it.familiyparking.app.serverClass.Car;

public class DataBaseHelper extends SQLiteOpenHelper{

    public static final String NAME_DB = "familypark_db";
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
                    "PRIMARY KEY ( "+CarTable.CAR_ID+" )"+" ) ; ";

    public static final String DROP_TABLE_GROUP = "DROP TABLE IF EXISTS "+ GroupTable.TABLE+" ;";
    public static final String DROP_TABLE_CAR = "DROP TABLE IF EXISTS "+ CarTable.TABLE+" ;";

    public DataBaseHelper(Context context){
        super(context, NAME_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_GROUP);
        db.execSQL(CREATE_TABLE_CAR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_GROUP);
        db.execSQL(DROP_TABLE_CAR);
        onCreate(db);
    }

}
