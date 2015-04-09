package it.familiyparking.app.dialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.adapter.CustomAdapterColor;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class ColorPickerDialog extends Fragment implements AdapterView.OnItemClickListener{

    MainActivity activity;
    private Car car;
    private GridView colorPicker;
    private CustomAdapterColor adapterColor;
    private Float color;

    public ColorPickerDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.color_picker, container, false);

        this.activity = (MainActivity) getActivity();

        Tools.resetUpButtonActionBar(activity);
        activity.resetMenu();

        Tools.closeKeyboard(activity);

        colorPicker = (GridView) rootView.findViewById(R.id.colorpicker);
        adapterColor = new CustomAdapterColor(activity,car.getMarkerColor());
        colorPicker.setAdapter(adapterColor);
        colorPicker.setOnItemClickListener(this);

        (rootView.findViewById(R.id.back_rl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        (rootView.findViewById(R.id.save_rl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveColor();
            }
        });

        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.car = args.getParcelable("car");
    }

    public void closeDialog(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.resetColorPicker();
            }
        });
    }

    public void saveColor(){
        car.setMarkerColor(color);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setBackgroundColorMarker(Tools.convertMarkerColor(color.floatValue()));
                activity.resetColorPicker();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        color = Tools.getMarkerColor().get(position);

        adapterColor = new CustomAdapterColor(activity,color);
        colorPicker.setAdapter(adapterColor);
        adapterColor.notifyDataSetChanged();
    }
}
