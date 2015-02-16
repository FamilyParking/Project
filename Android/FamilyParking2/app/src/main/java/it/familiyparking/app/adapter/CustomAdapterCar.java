package it.familiyparking.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.fragment.CarFragment;
import it.familiyparking.app.fragment.ManageCar;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.task.DoRemoveCar;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomAdapterCar extends ArrayAdapter<Car> implements View.OnLongClickListener{

    private MainActivity activity;
    private CarFragment carFragment;

    public CustomAdapterCar(Fragment fragment, Context context, ArrayList<Car> cars, Activity activity) {
        super(context, 0, cars);
        this.activity = (MainActivity)activity;
        this.carFragment = (CarFragment) fragment;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Car car = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.car_item, parent, false);
        }

        setBrand(convertView, car);

        setTextEdit(convertView,car);

        setOnLongClick(convertView,car);

        setOnClick(convertView,car);

        return convertView;
    }

    @Override
    public boolean onLongClick(View v) {
        RelativeLayout containerButton = (RelativeLayout) v.getRootView().findViewById(R.id.button_rl_car);
        View line = v.getRootView().findViewById(R.id.line);

        if(!containerButton.isShown()){
            containerButton.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);

            return true;
        }

        return false;
    }

    private void deleteCar(String carID){
        ProgressDialogCircular progressDialog = new ProgressDialogCircular();
        activity.setProgressDialogCircular(progressDialog);

        Bundle bundle = new Bundle();
        bundle.putString("message", "Deleting car ...");
        progressDialog.setArguments(bundle);

        activity.getSupportFragmentManager().beginTransaction().add(R.id.container, progressDialog).commit();

        new Thread(new DoRemoveCar(activity,carFragment,carID)).start();
    }

    private void setBrand(View convertView, Car car){
        ImageView brand_image = (ImageView) convertView.findViewById(R.id.car_iv);
        brand_image.setBackgroundDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier(car.getBrand(),"drawable",activity.getPackageName())));
    }

    private void setTextEdit(View convertView, Car car){
        ((TextView) convertView.findViewById(R.id.car_name_tv)).setText(car.getName());
    }

    private void setOnLongClick(View convertView, Car car){
        RelativeLayout container_item = (RelativeLayout) convertView.findViewById(R.id.car_item_container);
        container_item.setOnLongClickListener(this);
        container_item.setContentDescription(car.getId());

        (convertView.findViewById(R.id.relative_car_car)).setOnLongClickListener(this);
    }

    private void setOnClickDelete(View convertView){
        convertView.findViewById(R.id.delete_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCar(v.getRootView().findViewById(R.id.car_item_container).getContentDescription().toString());
            }
        });
    }

    private void setOnClickModify(View convertView, final Car car){
        convertView.findViewById(R.id.modify_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageCar modifyCar = new ManageCar();

                Bundle bundle = new Bundle();
                bundle.putParcelable("car",car);

                modifyCar.setArguments(bundle);

                activity.setModifyCar(modifyCar);

                activity.getSupportFragmentManager().beginTransaction().add(R.id.container, modifyCar).commit();
            }
        });
    }

    private void setOnClickBack(View convertView){
        convertView.findViewById(R.id.back_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getRootView().findViewById(R.id.button_rl_car).setVisibility(View.GONE);
                v.getRootView().findViewById(R.id.line).setVisibility(View.GONE);
            }
        });
    }

    private void setOnClick(View convertView,Car car){
        setOnClickBack(convertView);

        setOnClickModify(convertView,car);

        setOnClickDelete(convertView);
    }
}