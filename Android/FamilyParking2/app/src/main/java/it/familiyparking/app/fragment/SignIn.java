package it.familiyparking.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import it.familiyparking.app.R;
import it.familiyparking.app.task.AsyncTaskChangeButton;
import it.familiyparking.app.task.AsyncTaskLocationMap;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class SignIn extends Fragment implements TextWatcher{

    private EditText name_surname;
    private EditText email;
    private Button signIn;
    private boolean correctInput;

    public SignIn() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signin, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        name_surname = (EditText)rootView.findViewById(R.id.name_surname_et);
        name_surname.addTextChangedListener(this);

        email = (EditText)rootView.findViewById(R.id.email_et);
        email.addTextChangedListener(this);

        correctInput = false;

        signIn = (Button)rootView.findViewById(R.id.signIn_b);

        return rootView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after){}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){}

    @Override
    public void afterTextChanged(Editable s){
        if(email.getText().toString().contains("@") && (Tools.removeSpace(name_surname.getText().toString()).length() > 0)) {
            correctInput = true;

            Animation rotate_clockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clockwise);
            signIn.startAnimation(rotate_clockwise);

            new AsyncTaskChangeButton().execute(rotate_clockwise.getDuration()-1,signIn,getActivity(),R.drawable.arrow_blue_right,true);
        }
        else {
            if(correctInput) {
                Animation rotate_clockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_counterclockwise);
                signIn.startAnimation(rotate_clockwise);

                new AsyncTaskChangeButton().execute(rotate_clockwise.getDuration()-1,signIn,getActivity(),R.drawable.arrow_blue_up,false);
            }
            correctInput = false;
        }
    }
}
