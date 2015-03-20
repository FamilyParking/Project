package it.familiyparking.app.dialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class ProgressDialogCircularMain extends Fragment{

    private String text;
    private TextView textView;

    public ProgressDialogCircularMain() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.progress_circle_dialog, container, false);

        Tools.resetUpButtonActionBar((ActionBarActivity) getActivity());
        ((MainActivity) getActivity()).resetMenu();


        textView = (TextView) rootView.findViewById(R.id.message_dialog);
        textView.setText(text);

        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.text = args.getString("message");
    }

    public void updateMessage(String message){
        while(!updateMessagePrivate(message));
    }

    private boolean updateMessagePrivate(String message){
        this.text = message;
        if(textView != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(text);
                }
            });
            return true;
        }

        return false;
    }
}
