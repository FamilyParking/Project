package it.familiyparking.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import it.familiyparking.app.R;
import it.familiyparking.app.task.AsyncTaskLocationMap;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class Map extends Fragment{

    private GoogleMap googleMap;
    private Button toPark;
    private Button toCreate;
    private boolean afterPositionSettings;
    private Context context;
    private boolean setGraphic;

    public Map() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        Tools.resetUpButtonActionBar((ActionBarActivity) getActivity());

        setUpMap();

        toPark = (Button)rootView.findViewById(R.id.toPark);
        toCreate = (Button)rootView.findViewById(R.id.toCreate);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(setGraphic){
            enableGraphics();
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

    public void enableGraphics(){

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
            new AsyncTaskLocationMap().execute(googleMap, getActivity());

            toPark.setClickable(true);
            toPark.setVisibility(View.VISIBLE);

            toCreate.setClickable(true);
            toCreate.setVisibility(View.VISIBLE);
        }
    }
}
