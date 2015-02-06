package it.familiyparking.app.task;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import it.familiyparking.app.adapter.CustomHorizontalAdapter;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.serverClass.Contact;

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
        //ArrayList<Contact> list = GroupTable.getGroup(db);
        ArrayList<Contact> list = null;
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
