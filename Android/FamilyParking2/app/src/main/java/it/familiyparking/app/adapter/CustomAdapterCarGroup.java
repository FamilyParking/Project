package it.familiyparking.app.adapter;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Group;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomAdapterCarGroup extends ArrayAdapter<Car> {

    private MainActivity activity;
    private String groupID;

    public CustomAdapterCarGroup(Activity activity, ArrayList<Car> array, String groupID) {
        super(activity.getApplicationContext(), 0, array);
        this.activity = (MainActivity) activity;
        this.groupID = groupID;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.car_group_item, parent, false);
        }

        Button new_car = (Button) convertView.findViewById(R.id.add_car_button_item);
        RelativeLayout item = (RelativeLayout) convertView.findViewById(R.id.container_car_group);

        Car car = getItem(position);

        if(car.getId().equals("empty")){
            item.setVisibility(View.GONE);

            new_car.setVisibility(View.VISIBLE);
            new_car.setClickable(true);
            new_car.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.setNewCar(groupID);
                    activity.closeModifyGroup();
                }
            });

        }
        else {
            item.setVisibility(View.VISIBLE);
            new_car.setVisibility(View.GONE);

            /*RadioButton radio = (RadioButton) convertView.findViewById(R.id.car_radio_button);

            DataBaseHelper databaseHelper = new DataBaseHelper(activity);
            final SQLiteDatabase db = databaseHelper.getWritableDatabase();

            radio.setSelected(CarGroupRelationTable.isInRelation(db,car.getId(),groupID));

            db.close();*/

            ImageView brand_icon = (ImageView) convertView.findViewById(R.id.car_logo_item_iv);
            TextView name = (TextView) convertView.findViewById(R.id.car_name_item_tv);


            brand_icon.setBackgroundDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier(car.getBrand(), "drawable", activity.getPackageName())));
            name.setText(car.getName());
        }

        return convertView;
    }

}