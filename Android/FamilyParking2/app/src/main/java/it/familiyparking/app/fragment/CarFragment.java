package it.familiyparking.app.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import it.familiyparking.app.R;
import it.familiyparking.app.adapter.CustomAdapterCar;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class CarFragment extends Fragment{

    private ListView car_list;
    private ArrayList<Car> cars;
    private CustomAdapterCar customAdapterCar;

    public CarFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car, container, false);

        Tools.setUpButtonActionBar((ActionBarActivity)getActivity());

        DataBaseHelper databaseHelper = new DataBaseHelper(getActivity());
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();
        cars = CarTable.getAllCar(db);
        db.close();

        car_list = (ListView) rootView.findViewById(R.id.car_lv);
        customAdapterCar = new CustomAdapterCar(this,getActivity(),cars,getActivity());
        car_list.setAdapter(customAdapterCar);
        customAdapterCar.notifyDataSetChanged();

        return rootView;
    }

    public void updateAdapter(ArrayList<Car> newCarList){
        cars = newCarList;
        customAdapterCar = new CustomAdapterCar(this,getActivity(),newCarList,getActivity());
        car_list.setAdapter(customAdapterCar);
        customAdapterCar.notifyDataSetChanged();
    }

}
