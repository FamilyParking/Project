package it.familiyparking.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.task.AsyncTaskChangeButton;
import it.familiyparking.app.task.DoConfirmation;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class Confirmation extends Fragment implements TextWatcher,View.OnClickListener{

    private EditText code;
    private Button confirmation;
    private View progressCircle;
    private boolean correctInput;
    private boolean isRotated;

    public Confirmation() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirmation, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        code = (EditText)rootView.findViewById(R.id.confirmation_et);
        code.addTextChangedListener(this);

        correctInput = false;
        isRotated = false;

        confirmation = (Button)rootView.findViewById(R.id.confirmation_b);
        confirmation.setOnClickListener(this);

        progressCircle = rootView.findViewById(R.id.progress_confirmation);

        return rootView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after){}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){}

    @Override
    public void afterTextChanged(Editable s){
        if(code.getText().toString().length() > 0) {
            correctInput = true;

            if(!isRotated) {
                Animation rotate_clockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clockwise);
                confirmation.startAnimation(rotate_clockwise);
                isRotated = true;

                new AsyncTaskChangeButton().execute(rotate_clockwise.getDuration() - 10, confirmation, getActivity(), R.drawable.arrow_blue_right, true);
            }
        }
        else {
            if(correctInput) {
                Animation rotate_clockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_counterclockwise);
                confirmation.startAnimation(rotate_clockwise);
                isRotated = false;

                new AsyncTaskChangeButton().execute(rotate_clockwise.getDuration()-1,confirmation,getActivity(),R.drawable.arrow_blue_up,false);
            }
            correctInput = false;
        }
    }

    public void resetEditText(){
        code.setText("");
    }

    public void onClick(View v){
        MainActivity activity = (MainActivity) getActivity();

        Tools.closeKeyboard(v,activity);

        confirmation.setVisibility(View.GONE);
        progressCircle.setVisibility(View.VISIBLE);

        code.setKeyListener(null);

        new Thread(new DoConfirmation(this,activity.getUser(),code.getText().toString())).start();
    }

    public void endConfirmation(boolean error){

        if(error){
            confirmation.setVisibility(View.VISIBLE);
            progressCircle.setVisibility(View.GONE);

            resetEditText();
        }
        else{
            MainActivity activity = (MainActivity) getActivity();
            activity.endConfirmation();
        }
    }
}
