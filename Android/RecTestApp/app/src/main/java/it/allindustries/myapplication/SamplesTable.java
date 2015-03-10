package it.allindustries.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class SamplesTable {

    public static final String ID = "ID";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String TYPE = "TYPE";
    public static final String INFO = "INFO";
    public static final String CORRECT = "CORRECT";
    public static final String TIMESTAMP = "TIMESTAMP";
	public static final String[] COLUMNS = new String[]{ID,LATITUDE,LONGITUDE,TYPE,INFO,CORRECT,TIMESTAMP};

    public static final String TABLE = "samples_table";

    public static void insertSamples(Context context,int type, String info){
        DataBaseHelper databaseHelper = new DataBaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] value = (new Sample(context,type,info)).getArray();

        ContentValues v = new ContentValues();
        for(int i=0; i<value.length; i++)
            v.put(COLUMNS[i+1], value[i]);

        db.insert(TABLE, null, v);

        db.close();
    }

    public static ArrayList<Sample> getAllSamples(Context context) throws SQLException{
        DataBaseHelper databaseHelper = new DataBaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ArrayList<Sample> samples= new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, null, null, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            while(c.moveToNext())
                samples.add(new Sample(c.getString(0),c.getDouble(1),c.getDouble(2),c.getInt(3),c.getString(4),c.getInt(5),c.getString(6)));
        }

        c.close();

        db.close();

        return samples;
    }

    public static ArrayList<Sample> getAllSamplesParked(Context context) throws SQLException{
        DataBaseHelper databaseHelper = new DataBaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ArrayList<Sample> samples= new ArrayList<>();

        Cursor c = db.query(true, TABLE, COLUMNS, TYPE + " = " + Sample.PARKED, null, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            while(c.moveToNext())
                samples.add(new Sample(c.getString(0),c.getDouble(1),c.getDouble(2),c.getInt(3),c.getString(4),c.getInt(5),c.getString(6)));
        }

        c.close();

        db.close();

        return samples;
    }

    public static Sample getSample_ByID(Context context,String sampleID) throws SQLException{
        DataBaseHelper databaseHelper = new DataBaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor c = db.query(true, TABLE, COLUMNS, ID + " = ?", new String[]{ sampleID }, null, null, null, null);

        if((c != null) && (c.getCount() > 0)){
            if(c.moveToNext())
                return new Sample(c.getString(0),c.getDouble(1),c.getDouble(2),c.getInt(3),c.getString(4),c.getInt(5),c.getString(6));
        }

        c.close();

        db.close();

        return null;
    }

    public static int updateSample(Context context, String sampleID, int flag){
        DataBaseHelper databaseHelper = new DataBaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CORRECT, flag);

        int ret =  db.update(TABLE, values, ID + " = ?", new String[] { sampleID });

        db.close();

        return ret;
    }

    public static boolean deleteTable(SQLiteDatabase db){
        return db.delete(TABLE, null, null) > 0;
    }
}
