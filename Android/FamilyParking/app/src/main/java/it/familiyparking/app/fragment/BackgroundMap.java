package it.familiyparking.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;


/**
 * Created by francesco on 15/01/15.
 */
public class BackgroundMap extends Fragment{

    private MainActivity activity;
    private GoogleMap googleMap;

    public BackgroundMap() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        this.activity = (MainActivity) getActivity();

        rootView.findViewById(R.id.ghostmode_lable).setVisibility(View.GONE);

        setUpMap();

        return rootView;
    }

    private void setUpMap(){
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }

}
