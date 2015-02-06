package it.familiyparking.app.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{

    public static final String NAME_DB = "familypark_db";
    public static final int VERSION_DB = 1;

    public static final String CREATE_TABLE_GROUP =
            "CREATE TABLE IF NOT EXISTS "+ GroupTable.TABLE+" ( " +
                    GroupTable.ID+" INTEGER NOT NULL, "+
                    GroupTable.GROUP_ID+" TEXT NOT NULL, "+
                    GroupTable.GROUP_NAME+" TEXT NOT NULL, "+
                    GroupTable.EMAIL+" TEXT PRIMARY KEY, "+
                    GroupTable.HAS_PHOTO+" INTEGER DEFAULT 0, "+
                    GroupTable.PHOTO_ID+" TEXT NOT NULL "+" ) ; ";

    public static final String DROP_TABLE_GROUP = "DROP TABLE IF EXISTS "+ GroupTable.TABLE+" ;";

    public DataBaseHelper(Context context){
        super(context, NAME_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_GROUP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_GROUP);
        onCreate(db);
    }

}
