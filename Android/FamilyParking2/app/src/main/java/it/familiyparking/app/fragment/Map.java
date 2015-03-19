package it.familiyparking.app.fragment;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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
    private boolean setGraphic;
    private AlertDialog dialogSettings;
    private boolean moveToMyLocation;
    private ProgressBar progressBar;

    public Map() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        this.activity = (MainActivity) getActivity();

        Tools.resetUpButtonActionBar(activity);

        this.progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);

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
    public void setArguments(Bundle args) {
        super.setArguments(args);
        moveToMyLocation = (args.getString("car_id") == null);
    }

    @Override
    public void onStart() {
        super.onStart();

        if((dialogSettings != null) && (!dialogSettings.isShowing())){
            if (!Tools.isPositionHardwareEnable(getActivity())) {
                Tools.showClosedInfoAlert(getActivity());
            }
        }

        if((!activity.signInIsShown()) && (!toPark.isShown()) && (googleMap != null) && (googleMap.getCameraPosition().zoom < 10))
                new AsyncTaskLocationMap().execute(googleMap, getActivity(), moveToMyLocation, progressBar);
    }


    private void setUpMap(){
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    public void enableGraphics(boolean p_button){
        if(getActivity() == null) {
            setGraphic = true;
        }
        else {
            setGraphic = false;

            if(p_button)
                setPbutton();

            if (!Tools.isPositionHardwareEnable(getActivity())) {
                dialogSettings = Tools.showAlertPosition(getActivity());
            }

            new AsyncTaskLocationMap().execute(googleMap, getActivity(), moveToMyLocation, progressBar);
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

    public void parkCar(ArrayList<Car> cars, String car_id){
        googleMap.clear();

        for(Car car : cars){
            if(car.isParked()) {
                LatLng carPosition = new LatLng(Double.parseDouble(car.getLatitude()), Double.parseDouble(car.getLongitude()));
                googleMap.addMarker(new MarkerOptions().position(carPosition).title(car.getName()));

                if((car_id != null) && (car.getId().equals(car_id)))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(car.getLatitude()), Double.parseDouble(car.getLongitude())),18.0f));
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
