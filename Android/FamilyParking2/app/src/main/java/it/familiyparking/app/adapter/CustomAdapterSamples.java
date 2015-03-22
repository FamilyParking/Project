package it.familiyparking.app.adapter;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.NotifiedTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.parky.Notified;
import it.familiyparking.app.parky.StatisticActivity;
import it.familiyparking.app.parky.StatisticFragment;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomAdapterSamples extends ArrayAdapter<Notified> {

    private StatisticFragment fragment;
    private User user;
    private ArrayList<Car> cars;

    public CustomAdapterSamples(StatisticFragment fragment, ArrayList<Notified> list, User user, ArrayList<Car> cars) {
        super(fragment.getActivity().getApplicationContext(), 0, list);
        this.fragment = fragment;
        this.user = user;
        this.cars = cars;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.car_item, parent, false);
        }

        Notified notified = getItem(position);

        setMap(convertView, notified);
        setInfo(convertView, notified);
        setButton(convertView, notified);

        return convertView;
    }

    private void setMap(View convertView, Notified notified){
        GoogleMap googleMap = ((SupportMapFragment) fragment.getChildFragmentManager().findFragmentById(R.id.map_sample)).getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        double[] position = notified.getPosition();
        LatLng latLng = new LatLng(position[0],position[1]);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
    }

    private void setInfo(View convertView, Notified notified){
        String[] info = Tools.getDataTime(notified.getTimestamp());
        ((TextView) convertView.findViewById(R.id.sample_data_value)).setText(info[0]);
        ((TextView) convertView.findViewById(R.id.sample_time_value)).setText(info[1]);
    }

    private void setButton(View convertView, final Notified notified) {
        convertView.findViewById(R.id.toDiscard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = Tools.getDB_Writable(fragment.getActivity());
                NotifiedTable.deleteNotified(db,notified.getId());
                db.close();

                fragment.updateAdapter();
            }
        });

        convertView.findViewById(R.id.toAccept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.showAlertParking(fragment.getActivity(), cars, user, true, notified.getId());
            }
        });
    }

}