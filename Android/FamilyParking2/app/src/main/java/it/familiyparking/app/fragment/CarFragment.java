package it.familiyparking.app.fragment;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.adapter.CustomAdapterCar;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class CarFragment extends Fragment{

    private User user;
    private ArrayList<Car> carArrayList;
    private CustomAdapterCar customAdapterCar;
    private ListView listView;

    public CarFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car, container, false);

        Tools.setTitleActionBar((MainActivity) getActivity(), R.string.list_car);

        listView = (ListView) rootView.findViewById(R.id.car_lv);
        customAdapterCar = new CustomAdapterCar(getActivity(),carArrayList);
        listView.setAdapter(customAdapterCar);

        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.carArrayList = args.getParcelableArrayList("cars");
        this.user = args.getParcelable("user");
    }

    public void updateAdapter(ArrayList<Car> newCarList){
        carArrayList = newCarList;
        customAdapterCar = new CustomAdapterCar(getActivity(),carArrayList);
        listView.setAdapter(customAdapterCar);
        customAdapterCar.notifyDataSetChanged();
    }

}
