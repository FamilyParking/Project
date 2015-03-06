package it.familiyparking.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class GhostMode extends Fragment{

    private MainActivity activity;

    public GhostMode() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ghostmode, container, false);
        Tools.setUpButtonActionBar((ActionBarActivity) getActivity());

        activity = (MainActivity) getActivity();

        SQLiteDatabase db = Tools.getDB_Readable(activity);
        boolean isActive = UserTable.getGhostMode(db);
        db.close();

        alert(isActive);

        return rootView;
    }

    private void alert(final boolean isActive){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        alertDialog.setCancelable(false);

        alertDialog.setTitle("Ghostmode");

        if(isActive)
            alertDialog.setMessage("Do you want to DEACTIVATE the ghostmode function?");
        else
            alertDialog.setMessage("Do you want to ACTIVATE the ghostmode function?");

        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = Tools.getDB_Writable(activity);
                        UserTable.updateGhostmode(db,!isActive);
                        db.close();

                        resetGhostmodeFragment();

                        dialog.cancel();

                    }
                });

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        resetGhostmodeFragment();
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void resetGhostmodeFragment(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.resetGhostmode();
            }
        });
    }
}
