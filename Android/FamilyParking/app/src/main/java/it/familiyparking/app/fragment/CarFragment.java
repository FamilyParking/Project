package it.familiyparking.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

import it.familiyparking.app.FPApplication;
import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.adapter.CustomAdapterCar;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class CarFragment extends Fragment{

    MainActivity activity;

    private User user;
    private ArrayList<Car> carArrayList;
    private CustomAdapterCar customAdapterCar;
    private ListView listView;

    private RelativeLayout relativeNoCar;
    private ProgressWheel progess;
    private ImageView logoIv;
    private TextView infoTv;

    public CarFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car, container, false);

        activity = (MainActivity) getActivity();

        FPApplication application = (FPApplication) activity.getApplication();
        user = application.getUser();
        carArrayList = application.getCars();

        if(activity.getLunchWithEmptyList() && !application.isGetAllCar_Running()){
            activity.setCreateCar();
            Tools.createToast(activity,activity.getResources().getString(R.string.empty_car), Toast.LENGTH_LONG);
        }

        listView = (ListView) rootView.findViewById(R.id.car_lv);

        relativeNoCar = (RelativeLayout) rootView.findViewById(R.id.car_rl);
        logoIv = (ImageView) rootView.findViewById(R.id.logo_car_iv);
        infoTv = (TextView) rootView.findViewById(R.id.info_car_tv);
        progess = (ProgressWheel) rootView.findViewById(R.id.car_info_progress);

        setData();

        rootView.findViewById(R.id.to_create_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setCreateCar();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.hideMyPosition();
        Tools.setTitleActionBar(activity,R.string.list_car);
    }

    private void setData(){
        if(carArrayList.isEmpty()){
            listView.setVisibility(View.GONE);
            relativeNoCar.setVisibility(View.VISIBLE);

            if(((FPApplication)activity.getApplication()).isGetAllCar_Running()) {
                logoIv.setVisibility(View.GONE);
                progess.setVisibility(View.VISIBLE);
                infoTv.setText(activity.getResources().getString(R.string.update_car_list));
            }
            else{
                logoIv.setVisibility(View.VISIBLE);
                progess.setVisibility(View.GONE);
                infoTv.setText(activity.getResources().getString(R.string.no_car));
            }
        }
        else {
            relativeNoCar.setVisibility(View.GONE);

            listView.setVisibility(View.VISIBLE);
            customAdapterCar = new CustomAdapterCar(getActivity(), carArrayList);
            listView.setAdapter(customAdapterCar);
        }
    }

    public void updateAdapter(ArrayList<Car> newCarList){
        if(activity != null) {
            carArrayList = newCarList;
            customAdapterCar = new CustomAdapterCar(activity, carArrayList);
            listView.setAdapter(customAdapterCar);
            customAdapterCar.notifyDataSetChanged();

            setData();
        }
    }
}
