package com.familyparking.app.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.familyparking.app.R;
import com.familyparking.app.adapter.CustomHorizontalAdapter;
import com.familyparking.app.serverClass.Contact;
import com.familyparking.app.utility.Tools;

/**
 * Created by francesco on 17/03/14.
 */

public class ProgressCircleDialog extends DialogFragment{

    private String text;
    private TextView textView;

    public ProgressCircleDialog(String message){
        this.text = message;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        this.setCancelable(false);

        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.progress_circle_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textView = (TextView) dialog.findViewById(R.id.message_dialog);
        textView.setText(text);

        dialog.show();

        return dialog;
    }

    public void updateMessage(String message){
        textView.setText(message);
    }



}