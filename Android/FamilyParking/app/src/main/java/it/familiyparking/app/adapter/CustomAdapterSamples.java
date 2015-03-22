package it.familiyparking.app.adapter;

import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import it.familiyparking.app.R;
import it.familiyparking.app.dao.NotifiedTable;
import it.familiyparking.app.parky.Notified;
import it.familiyparking.app.parky.StatisticFragment;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomAdapterSamples extends ArrayAdapter<Notified> implements View.OnClickListener{

    private StatisticFragment fragment;
    private User user;
    private ArrayList<Car> cars;
    ArrayList<Notified> list;
    private View lastSelected;

    public CustomAdapterSamples(StatisticFragment fragment, ArrayList<Notified> list, User user, ArrayList<Car> cars) {
        super(fragment.getActivity().getApplicationContext(), 0, list);
        this.fragment = fragment;
        this.user = user;
        this.cars = cars;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notified_statistic, parent, false);
        }

        Notified notified = getItem(position);

        setRoot(convertView, notified);
        setInfo(convertView, notified);
        setButton(convertView, notified);

        if(position == 0){
            fragment.setMarker(new LatLng(Double.parseDouble(notified.getLatitude()), Double.parseDouble(notified.getLongitude())));
            RelativeLayout relativeLayout = ((RelativeLayout) convertView.findViewById(R.id.sample_root));
            relativeLayout.setBackgroundDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.card_layout));
            lastSelected = relativeLayout;
        }

        return convertView;
    }

    private void setRoot(View convertView, Notified notified){
        final RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.sample_root);
        relativeLayout.setContentDescription(notified.getId());
        relativeLayout.setBackgroundDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.card_layout_dark));
        relativeLayout.setOnClickListener(this);
    }

    private void setInfo(View convertView, Notified notified){
        String[] info = Tools.getDataTime(notified.getTimestamp());
        ((TextView) convertView.findViewById(R.id.sample_data_value)).setText(info[0]);
        ((TextView) convertView.findViewById(R.id.sample_time_value)).setText(info[1]);
    }

    private void setButton(View convertView, Notified notified) {
        Button discard = (Button) convertView.findViewById(R.id.toDiscard);
        discard.setContentDescription(notified.getId());
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = Tools.getDB_Writable(fragment.getActivity());
                NotifiedTable.deleteNotified(db, v.getContentDescription().toString());
                db.close();

                fragment.updateData();
            }
        });

        Button accept = (Button) convertView.findViewById(R.id.toAccept);
        accept.setContentDescription(notified.getId());
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setDialog(Tools.showAlertParking(fragment.getActivity(), cars, user, true, v.getContentDescription().toString()));
            }
        });
    }

    @Override
    public void onClick(View v) {
        for(Notified n : list){
            if(n.getId().equals(v.getContentDescription().toString())) {
                if(!lastSelected.getContentDescription().toString().equals(n.getId())) {
                    fragment.setMarker(new LatLng(Double.parseDouble(n.getLatitude()), Double.parseDouble(n.getLongitude())));

                    v.setBackgroundDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.card_layout));
                    lastSelected.setBackgroundDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.card_layout_dark));
                    setLastSelected(v);
                }
            }
        }
    }

    private void setLastSelected(View v){
        lastSelected = v;
    }



}