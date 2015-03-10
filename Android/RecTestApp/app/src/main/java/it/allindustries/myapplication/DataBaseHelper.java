package it.allindustries.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{

    public static final String NAME_DB = "test_db";
    public static final int VERSION_DB = 1;

    public static final String CREATE_TABLE_SAMPLE =
            "CREATE TABLE IF NOT EXISTS "+ SamplesTable.TABLE+" ( " +
                    SamplesTable.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    SamplesTable.LATITUDE+" REAL NOT NULL, "+
                    SamplesTable.LONGITUDE+" REAL NOT NULL, "+
                    SamplesTable.TYPE+" INTEGER NOT NULL, "+
                    SamplesTable.INFO+" TEXT NOT NULL, "+
                    SamplesTable.CORRECT+" INTEGER NOT NULL, "+
                    SamplesTable.TIMESTAMP+" TEXT NOT NULL ) ; ";

    public static final String DROP_TABLE_SAMPLE = "DROP TABLE IF EXISTS "+ SamplesTable.TABLE+" ;";

    public DataBaseHelper(Context context){
        super(context, NAME_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_SAMPLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_SAMPLE);
        onCreate(db);
    }

}
