package it.familiyparking.app.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.task.AsyncTaskLocationMap;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class Map extends Fragment{

    private MainActivity activity;
    private GoogleMap googleMap;
    private Button toPark;
    private RelativeLayout ghostmodeLable;
    private boolean afterPositionSettings;
    private boolean setGraphic;

    public Map() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        this.activity = (MainActivity) getActivity();

        Tools.resetUpButtonActionBar(activity);

        setUpMap();

        toPark = (Button)rootView.findViewById(R.id.toPark);

        ghostmodeLable = (RelativeLayout)rootView.findViewById(R.id.ghostmode_lable);
        updateGhostmodeLable();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(setGraphic){
            enableGraphics(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(afterPositionSettings){
            if(Tools.isPositionHardwareEnable(getActivity()))
                afterPositionSettings = false;
            else
                Tools.showClosedInfoAlert(getActivity());
        }
    }

    private void setUpMap(){
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }

    public void enableGraphics(boolean p_button){

        if(getActivity() == null) {
            setGraphic = true;
        }
        else {
            setGraphic = false;

            if (!Tools.isPositionHardwareEnable(getActivity())) {
                afterPositionSettings = true;
                Tools.showAlertPosition(getActivity());
            }

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            new AsyncTaskLocationMap().execute(googleMap, getActivity());

            if(p_button)
                setPbutton();
        }
    }

    public void setPbutton(){
        toPark.setClickable(true);
        toPark.setVisibility(View.VISIBLE);
    }

    public String getLatitude(){
        return Double.toString(googleMap.getMyLocation().getLatitude());
    }

    public String getLongitude(){
        return Double.toString(googleMap.getMyLocation().getLongitude());
    }

    public void parkCar(Car toPark, boolean moveCamera){
        googleMap.clear();

        SQLiteDatabase db = Tools.getDB_Readable(activity);
        ArrayList<Car> cars = CarTable.getAllCar(db);
        db.close();

        for(Car car : cars) {
            if(car.isParked()) {
                LatLng carPosition = new LatLng(Double.parseDouble(car.getLatitude()), Double.parseDouble(car.getLongitude()));

                googleMap.addMarker(new MarkerOptions().position(carPosition).title(car.getName()));

                if ((moveCamera) && (toPark.equals(car))) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(carPosition));
                }
            }
        }
    }

    public void parkCar(ArrayList<Car> cars){
        googleMap.clear();

        for(Car car : cars){
            if(car.isParked()) {
                LatLng carPosition = new LatLng(Double.parseDouble(car.getLatitude()), Double.parseDouble(car.getLongitude()));
                googleMap.addMarker(new MarkerOptions().position(carPosition).title(car.getName()));
            }
        }
    }

    public void moveCamera(LatLng position){
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    public void updatePosition(){
        if(googleMap != null) {
            Location location = googleMap.getMyLocation();

            while(location == null)
                location = googleMap.getMyLocation();

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }

    public void updateGhostmodeLable(){
        User user = activity.getUser();
        if((user == null) || (!user.isGhostmode()))
            ghostmodeLable.setVisibility(View.GONE);
        else
            ghostmodeLable.setVisibility(View.VISIBLE);
    }
}
