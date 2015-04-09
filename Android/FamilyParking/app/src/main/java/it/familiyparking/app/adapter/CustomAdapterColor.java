package it.familiyparking.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomAdapterColor extends ArrayAdapter<Float> {

    private MainActivity activity;
    private Float current;

    public CustomAdapterColor(Activity activity, Float current) {
        super(activity.getApplicationContext(), 0, Tools.getMarkerColor());
        this.activity = (MainActivity) activity;
        this.current = current;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            Float color = getItem(position);

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.color_item, parent, false);

            convertView.findViewById(R.id.color_item_root).setBackgroundColor(Tools.convertMarkerColor(color.floatValue()));

            if(color.equals(current))
                convertView.findViewById(R.id.color_selected).setVisibility(View.VISIBLE);
            else
                convertView.findViewById(R.id.color_selected).setVisibility(View.GONE);
        }

        return convertView;
    }
}