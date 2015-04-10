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
import it.familiyparking.app.task.DoSignIn;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class SignIn extends Fragment implements TextWatcher,View.OnClickListener{

    MainActivity activity;

    private EditText name_surname;
    private EditText email;
    private Button signIn;
    private View progressCircle;
    private boolean correctInput;
    private boolean isRotated;

    public SignIn() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signin, container, false);

        this.activity = (MainActivity) getActivity();

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        name_surname = (EditText)rootView.findViewById(R.id.name_surname_et);
        name_surname.addTextChangedListener(this);

        email = (EditText)rootView.findViewById(R.id.email_et);
        email.addTextChangedListener(this);

        correctInput = false;
        isRotated = false;

        signIn = (Button)rootView.findViewById(R.id.signIn_b);
        signIn.setOnClickListener(this);
        signIn.setClickable(false);

        progressCircle = rootView.findViewById(R.id.progress_signIn);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Tools.setTitleActionBar(activity,R.string.signin);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after){}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){}

    @Override
    public void afterTextChanged(Editable s){
        if(Tools.isEmailValid(email.getText().toString()) && (Tools.removeSpace(name_surname.getText().toString()).length() > 0)) {
            correctInput = true;

            if(!isRotated) {
                Animation rotate_clockwise = AnimationUtils.loadAnimation(activity, R.anim.rotate_clockwise);
                signIn.startAnimation(rotate_clockwise);
                isRotated = true;

                new AsyncTaskChangeButton().execute(rotate_clockwise.getDuration() - 10, signIn, activity, R.drawable.arrow_blue_right, true);
            }
        }
        else {
            if(correctInput) {
                Animation rotate_clockwise = AnimationUtils.loadAnimation(activity, R.anim.rotate_counterclockwise);
                signIn.startAnimation(rotate_clockwise);

                signIn.setClickable(true);

                isRotated = false;

                new AsyncTaskChangeButton().execute(rotate_clockwise.getDuration()-1,signIn,activity,R.drawable.arrow_blue_up,false);
            }
            else{
                signIn.setClickable(false);
            }

            correctInput = false;
        }
    }

    @Override
    public void onClick(View v) {
        Tools.closeKeyboard(v,activity);

        signIn.setVisibility(View.GONE);
        progressCircle.setVisibility(View.VISIBLE);

        name_surname.setKeyListener(null);
        email.setKeyListener(null);

        new Thread(new DoSignIn(this,name_surname.getText().toString(),email.getText().toString())).start();
    }

    public void endSignIn(boolean error){
        if(error){
            signIn.setVisibility(View.VISIBLE);
            progressCircle.setVisibility(View.GONE);

            resetEditText();
        }
        else{
            activity.endSignIn();
        }
    }

    private void resetEditText(){
        name_surname.setText("");
        email.setText("");
    }

}
