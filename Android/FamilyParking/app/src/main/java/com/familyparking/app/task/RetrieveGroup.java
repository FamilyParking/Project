package com.familyparking.app.task;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;

import com.familyparking.app.adapter.CustomHorizontalAdapter;
import com.familyparking.app.dao.DataBaseHelper;
import com.familyparking.app.dao.GroupTable;
import com.familyparking.app.serverClass.Contact;

import java.util.ArrayList;

/**
 * Created by francesco on 02/01/15.
 */
public class RetrieveGroup implements Runnable {

    private Context context;
    private RelativeLayout relativeTwoWayView;
    private CustomHorizontalAdapter customHorizontalAdapter;

    public RetrieveGroup(Context context, RelativeLayout relativeTwoWayView, CustomHorizontalAdapter customHorizontalAdapter) {
        this.context = context;
        this.relativeTwoWayView = relativeTwoWayView;
        this.customHorizontalAdapter = customHorizontalAdapter;
    }

    @Override
    public void run() {
        Looper.prepare();

        DataBaseHelper databaseHelper = new DataBaseHelper(context);
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();
        ArrayList<Contact> list = GroupTable.getGroup(db);
        db.close();

        for(final Contact contact : list) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    customHorizontalAdapter.add(contact, false);
                    customHorizontalAdapter.notifyDataSetChanged();
                    if(relativeTwoWayView.getVisibility() == View.GONE) {
                        relativeTwoWayView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }
}
