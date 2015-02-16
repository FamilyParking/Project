package it.familiyparking.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.adapter.CustomAdapterCarBrand;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.task.DoSaveCar;
import it.familiyparking.app.task.DoUpdateCar;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class ManageCar extends Fragment implements TextWatcher{

    private Spinner spinner;
    private CustomAdapterCarBrand adapterCarBrand;

    private EditText editTextCar;
    private Button save_button;

    private View rootView;

    private Car car;

    public ManageCar() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_manage_car, container, false);

        setCarLayout();

        if(modifing()){
            setButtonModify();
            showInfo();
        }
        else{
            setButtonSave();
        }

        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        car = args.getParcelable("car");
    }

    private boolean modifing(){
        return car != null;
    }


    private void saveCar(){
        Tools.closeKeyboard(rootView,getActivity());

        ProgressDialogCircular progressDialog = new ProgressDialogCircular();
        ((MainActivity)getActivity()).setProgressDialogCircular(progressDialog);

        Bundle bundle = new Bundle();
        bundle.putString("message", "Creating car ...");
        progressDialog.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, progressDialog).commit();

        car = new Car(editTextCar.getText().toString(),Tools.getBrand(spinner,getActivity()));

        new Thread(new DoSaveCar(getActivity(),car)).start();
    }

    private void setCarLayout(){
        spinner = (Spinner) rootView.findViewById(R.id.car_sp);
        adapterCarBrand = new CustomAdapterCarBrand(getActivity());
        spinner.setAdapter(adapterCarBrand);

        editTextCar = (EditText) rootView.findViewById(R.id.car_name_et);
        editTextCar.addTextChangedListener(this);
    }

    private void setButtonSave(){
        save_button = (Button) rootView.findViewById(R.id.check_car_bt);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave();
            }
        });

        save_button.setClickable(false);
    }

    private void setButtonModify(){
        rootView.findViewById(R.id.car_rl).setVisibility(View.GONE);
        rootView.findViewById(R.id.container_button).setVisibility(View.VISIBLE);

        rootView.findViewById(R.id.back_car_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack();
            }
        });

        rootView.findViewById(R.id.save_car_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCar();
            }
        });
    }

    private void manageSaveButton(){
        if(editTextCar.getText().toString().isEmpty()) {
            save_button.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_grey));
            save_button.setClickable(false);
        }
        else {
            save_button.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_green));
            save_button.setClickable(true);
        }
    }

    private void onClickSave(){
        saveCar();
    }

    private void onClickBack(){

    }

    private void showInfo(){
        editTextCar.setText(car.getName());
        spinner.setSelection(Tools.getBrandIndex(car.getBrand(),getActivity()));
    }

    private void updateCar(){
        Tools.closeKeyboard(rootView,getActivity());

        ProgressDialogCircular progressDialog = new ProgressDialogCircular();
        ((MainActivity)getActivity()).setProgressDialogCircular(progressDialog);

        Bundle bundle = new Bundle();
        bundle.putString("message", "Updating car ...");
        progressDialog.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, progressDialog).commit();

        new Thread(new DoUpdateCar(getActivity(),this,editTextCar.getText().toString(),Tools.getBrand(spinner,getActivity()),car)).start();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        manageSaveButton();
    }
}
