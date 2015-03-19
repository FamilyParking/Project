package it.familiyparking.app.fragment;

import android.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.task.AsyncTaskLocationMap;
import it.familiyparking.app.task.DoFixPosition;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class FixPosition extends Fragment implements GoogleMap.OnMarkerDragListener{

    private MainActivity activity;

    private GoogleMap googleMap;
    private User user;
    private Car car;

    private RelativeLayout toSave;
    private RelativeLayout toBack;

    public FixPosition() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fixposition, container, false);

        this.activity = (MainActivity) getActivity();

        setUpMap();

        toSave = (RelativeLayout)rootView.findViewById(R.id.save_fix_rl);
        toSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new DoFixPosition(activity,user,car)).start();
            }
        });
        toSave.setClickable(true);

        toBack = (RelativeLayout)rootView.findViewById(R.id.back_fix_rl);
        toBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.resetFixPosition();
            }
        });
        toBack.setClickable(true);

        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.user = args.getParcelable("user");
        this.car = args.getParcelable("car");
    }


    private void setUpMap(){
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setMyLocationEnabled(true);

            googleMap.setOnMarkerDragListener(this);

            LatLng carPosition = new LatLng(Double.parseDouble(car.getLatitude()), Double.parseDouble(car.getLongitude()));
            googleMap.addMarker(new MarkerOptions().position(carPosition).title(car.getName()).draggable(true));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(car.getLatitude()), Double.parseDouble(car.getLongitude())),18.0f));
        }
    }


    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng position = marker.getPosition();
        car.setPosition(new double[]{position.latitude,position.longitude});
    }

    @Override
    public void onMarkerDragStart(Marker marker) {}
}
