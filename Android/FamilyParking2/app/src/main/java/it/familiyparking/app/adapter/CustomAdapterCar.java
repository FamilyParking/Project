package it.familiyparking.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomAdapterCar extends ArrayAdapter<Car> {

    private MainActivity activity;

    public CustomAdapterCar(Activity activity, ArrayList<Car> list) {
        super(activity.getApplicationContext(), 0, list);
        this.activity = (MainActivity) activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Car car = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_4car_item, parent, false);
        }

        setBrand(convertView,car);
        setName(convertView,car);
        setRegister(convertView, car);
        setBluetooth(convertView,car);
        setContactList(convertView,car);
        setDetailButton(convertView,car);

        return convertView;
    }

    private void setBrand(View convertView, Car car){
        ImageView brand = (ImageView) convertView.findViewById(R.id.car_brand_iv);
        brand.setBackgroundDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier(car.getBrand(),"drawable",activity.getPackageName())));
    }

    private void setName(View convertView, Car car){
        TextView name = (TextView) convertView.findViewById(R.id.car_name_tv);
        name.setText(car.getName());
    }

    private void setRegister(View convertView, Car car) {
        TextView register = (TextView) convertView.findViewById(R.id.car_register_tv);
        if(car.getRegister() != null)
            register.setText(car.getRegister());
    }

    private void setBluetooth(View convertView, Car car) {
        ImageView bluetooth = (ImageView) convertView.findViewById(R.id.car_bluetooth_iv);
        if(car.getBluetoothMac() != null)
            bluetooth.setVisibility(View.VISIBLE);
        else
            bluetooth.setVisibility(View.GONE);
    }

    private void setContactList(View convertView, Car car) {
        TwoWayView contact_list = (TwoWayView) convertView.findViewById(R.id.group_list);
        CustomHorizontalAdapter_4CarItem customHorizontalAdapter = new CustomHorizontalAdapter_4CarItem(activity,car.getUsers());
        contact_list.setAdapter(customHorizontalAdapter);
    }

    private void setDetailButton(View convertView, Car car){
        Button details_button = (Button) convertView.findViewById(R.id.car_arrow_iv);
        details_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to car details
            }
        });
    }
}