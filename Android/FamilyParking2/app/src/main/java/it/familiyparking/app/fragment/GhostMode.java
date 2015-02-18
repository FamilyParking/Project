package it.familiyparking.app.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import it.familiyparking.app.R;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class GhostMode extends Fragment implements CompoundButton.OnCheckedChangeListener{

    private Switch switchWidget;

    public GhostMode() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ghostmode, container, false);
        Tools.setUpButtonActionBar((ActionBarActivity) getActivity());

        switchWidget = (Switch) rootView.findViewById(R.id.ghostmode_s);
        switchWidget.setOnCheckedChangeListener(this);

        DataBaseHelper databaseHelper = new DataBaseHelper(getActivity());
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();

        switchWidget.setChecked(UserTable.getGhostMode(db));

        db.close();

        return rootView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        DataBaseHelper databaseHelper = new DataBaseHelper(getActivity());
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();

        UserTable.updateGhostmode(db,isChecked);

        db.close();
    }
}
