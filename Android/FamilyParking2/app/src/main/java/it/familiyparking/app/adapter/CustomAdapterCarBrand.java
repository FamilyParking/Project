package it.familiyparking.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomAdapterCarBrand extends ArrayAdapter<String> {

    private MainActivity activity;

    public CustomAdapterCarBrand(Activity activity) {
        super(activity.getApplicationContext(), 0, activity.getResources().getStringArray(R.array.car_brands));
        this.activity = (MainActivity) activity;
    }

    @Override public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.car_brand_item, parent, false);
        }

        ImageView brand_icon = (ImageView) convertView.findViewById(R.id.car_logo_item_iv);
        brand_icon.setBackgroundDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier(getItem(position),"drawable",activity.getPackageName())));

        return convertView;
    }

}