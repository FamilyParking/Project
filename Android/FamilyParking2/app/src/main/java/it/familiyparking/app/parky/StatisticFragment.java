package it.familiyparking.app.parky;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

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

    private StatisticActivity activity;
    private View rootView;
    private CustomAdapterSamples adapter;
    private GoogleMap googleMap;
    private ListView listView;
    private ArrayList<Notified> nofieds;
    private User user;
    private ArrayList<Car> cars;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_statistic, container, false);

        activity = (StatisticActivity) getActivity();

        SQLiteDatabase db = Tools.getDB_Readable(activity);
        user = UserTable.getUser(db);
        cars = CarTable.getAllCar(db);
        nofieds = NotifiedTable.getAllNotified(db);
        db.close();

        adapter = new CustomAdapterSamples(this,nofieds,user,cars);
        listView = (ListView) rootView.findViewById(R.id.list_samples);
        listView.setAdapter(adapter);

        setUpMap();

        return rootView;
    }

    private void setUpMap(){
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_samples)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }

    public void setMarker(LatLng position){
        if (googleMap != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(position));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18.0f));
        }
    }

    public void updateAdapter(ArrayList<Notified> nofieds){
        this.nofieds = nofieds;
        adapter = new CustomAdapterSamples(this,nofieds,user,cars);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(adapter.isEmpty()){
            Tools.createToast(activity, "Statistics updated correctly", Toast.LENGTH_SHORT);
            activity.finish();
        }
        else{
            SQLiteDatabase db = Tools.getDB_Readable(activity);
            updateAdapter(NotifiedTable.getAllNotified(db));
            db.close();
        }
    }
}
