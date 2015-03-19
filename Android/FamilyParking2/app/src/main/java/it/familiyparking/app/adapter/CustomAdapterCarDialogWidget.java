package it.familiyparking.app.adapter;

import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import it.familiyparking.app.R;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.Tools;
import it.familiyparking.app.widget.DoParkByWidget;
import it.familiyparking.app.widget.DoUnparkByWidget;
import it.familiyparking.app.widget.WidgetActivity;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomAdapterCarDialogWidget extends ArrayAdapter<Car> {

    private WidgetActivity activity;
    private User user;
    private String action;

    public CustomAdapterCarDialogWidget(WidgetActivity activity, ArrayList<Car> list, User user, String action) {
        super(activity, 0, list);
        this.activity = activity;
        this.user = user;
        this.action = action;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.car_item_dialog, parent, false);
        }

        Car car = getItem(position);

        setRoot(convertView,car);
        setBrand(convertView,car);
        setName(convertView,car);
        setRegister(convertView, car);

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
        if(car.getRegister() != null) {
            register.setText(car.getRegister());
            register.setVisibility(View.VISIBLE);
        }
        else {
            register.setVisibility(View.GONE);
        }
    }

    private void setRoot(View convertView, Car car) {
        RelativeLayout relativeRoot = ((RelativeLayout)convertView.findViewById(R.id.car_item_root));
        relativeRoot.setContentDescription(car.getId());
        relativeRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = Tools.getDB_Readable(activity);
                Car car = CarTable.getCar_byID(db,v.getContentDescription().toString());
                db.close();

                if(action.equals(Code.TYPE_PARK))
                    new Thread(new DoParkByWidget(activity,user,car)).start();
                else if(action.equals(Code.TYPE_UNPARK))
                    new Thread(new DoUnparkByWidget(activity,user,car)).start();
            }
        });
    }
}