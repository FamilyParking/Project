package it.familiyparking.app.dialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
public class ProgressDialogCircular extends Fragment{

    private String text;
    private TextView textView;

    public ProgressDialogCircular() {}

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

    public boolean updateMessage(String message){
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
