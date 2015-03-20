package it.familiyparking.app.widget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import it.familiyparking.app.R;
import it.familiyparking.app.adapter.CustomAdapterCarDialogWidget;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.dialog.ProgressDialogCircularMain;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;


public class WidgetActivity extends FragmentActivity {

    private Tracker tracker;
    private AlertDialog dialog;
    private ProgressDialogCircular progressDialogCircular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tracker = Tools.activeAnalytic(this);

        SQLiteDatabase db = Tools.getDB_Readable(this);
        User user = UserTable.getUser(db);
        ArrayList<Car> cars = CarTable.getAllCar(db);
        db.close();

        dialog = createDialog(cars,user,getIntent().getStringExtra("action"));
    }

    @Override
    public void onBackPressed() {
        Tools.closeApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!dialog.isShowing())
            Tools.closeApp(this);
    }

    private AlertDialog createDialog(ArrayList<Car> cars, User user, String action){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setCancelable(true);

        alertDialog.setTitle("Pick the car");

        alertDialog.setAdapter(new CustomAdapterCarDialogWidget(this,cars,user,action),null);

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialogClass = alertDialog.create();

        alertDialogClass.show();

        return alertDialogClass;
    }

    public void closeDialog(){
        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void setProgressDialogCircular(String message){
        progressDialogCircular = new ProgressDialogCircular();

        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        progressDialogCircular.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.container, progressDialogCircular).commit();
    }

    public void resetProgressDialogCircular(){
        if(progressDialogCircular != null) {
            getSupportFragmentManager().beginTransaction().remove(progressDialogCircular).commit();
            progressDialogCircular = null;
        }
    }

}