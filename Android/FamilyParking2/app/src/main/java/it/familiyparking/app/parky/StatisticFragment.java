package it.familiyparking.app.parky;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.adapter.CustomAdapterSamples;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.NotifiedTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 17/03/15.
 */
public class StatisticFragment extends Fragment{

    View rootView;
    private CustomAdapterSamples adapter;
    StatisticActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_car_detail, container, false);

        activity = (StatisticActivity) getActivity();

        SQLiteDatabase db = Tools.getDB_Readable(activity);
        User user = UserTable.getUser(db);
        ArrayList<Car> cars = CarTable.getAllCar(db);
        ArrayList<Notified> nofieds = NotifiedTable.getAllNotified(db);
        db.close();

        adapter = new CustomAdapterSamples(this,nofieds,user,cars);

        ((ListView) rootView.findViewById(R.id.list_samples)).setAdapter(adapter);

        return rootView;
    }

    public void updateAdapter(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(adapter.isEmpty()){
            Tools.createToast(activity, "Statistics updated correctly", Toast.LENGTH_SHORT);
            activity.finish();
        }
    }
}
