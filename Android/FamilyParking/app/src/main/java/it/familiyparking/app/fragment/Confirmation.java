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
import android.widget.RelativeLayout;

import it.familiyparking.app.FPApplication;
import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.task.AsyncTaskChangeButton;
import it.familiyparking.app.task.DoConfirmation;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class Confirmation extends Fragment implements TextWatcher,View.OnClickListener{

    MainActivity activity;
    private EditText code;
    private Button confirmation;
    private RelativeLayout resetEmail;
    private View progressCircle;
    private boolean correctInput;
    private boolean isRotated;

    public Confirmation() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirmation, container, false);

        this.activity = (MainActivity) getActivity();

        Tools.setTitleActionBar(activity,R.string.confirmation);

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        code = (EditText)rootView.findViewById(R.id.confirmation_et);
        code.addTextChangedListener(this);

        correctInput = false;
        isRotated = false;

        confirmation = (Button)rootView.findViewById(R.id.confirmation_b);
        confirmation.setOnClickListener(this);

        resetEmail = (RelativeLayout)rootView.findViewById(R.id.reset_email_relative);
        resetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.invalidDB(activity);

                        activity.resetConfirmation(false);
                        activity.setSignIn();
                        Tools.setTitleActionBar(activity,R.string.signin);
                    }
                });
            }
        });

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
                Animation rotate_clockwise = AnimationUtils.loadAnimation(activity, R.anim.rotate_clockwise);
                confirmation.startAnimation(rotate_clockwise);
                isRotated = true;

                new AsyncTaskChangeButton().execute(rotate_clockwise.getDuration() - 10, confirmation, activity, R.drawable.arrow_blue_right, true);
            }
        }
        else {
            if(correctInput) {
                Animation rotate_clockwise = AnimationUtils.loadAnimation(activity, R.anim.rotate_counterclockwise);
                confirmation.startAnimation(rotate_clockwise);
                isRotated = false;

                new AsyncTaskChangeButton().execute(rotate_clockwise.getDuration()-1,confirmation,activity,R.drawable.arrow_blue_up,false);
            }
            correctInput = false;
        }
    }

    public void resetEditText(){
        code.setText("");
    }

    public void onClick(View v){
        Tools.closeKeyboard(v,activity);

        confirmation.setVisibility(View.GONE);
        resetEmail.setVisibility(View.GONE);
        progressCircle.setVisibility(View.VISIBLE);

        code.clearFocus();
        code.setClickable(false);

        new Thread(new DoConfirmation(this,((FPApplication) activity.getApplication()).getUser(),code.getText().toString())).start();
    }

    public void endConfirmation(boolean error){

        if(error){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    code.setClickable(true);

                    confirmation.setVisibility(View.VISIBLE);
                    resetEmail.setVisibility(View.VISIBLE);
                    progressCircle.setVisibility(View.GONE);

                    resetEditText();
                }
            });
        }
        else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.endConfirmation();
                }
            });
        }
    }
}
