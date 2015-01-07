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

public class ContactDetailDialog extends DialogFragment{

    private Contact contact;
    private int position;
    private CustomHorizontalAdapter adapter;
    private RelativeLayout relativeTwoWayView;

    public ContactDetailDialog(CustomHorizontalAdapter adapter, int position, RelativeLayout relativeTwoWayView){
        this.position = position;
        this.adapter = adapter;
        this.contact = adapter.getItem(position);
        this.relativeTwoWayView = relativeTwoWayView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        this.setCancelable(false);

        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.contact_detail_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(contact.getEmail().equals(contact.getName())){
            ((TextView) dialog.findViewById(R.id.contact_email_tv_detail)).setVisibility(View.GONE);
        }
        else{
            ((TextView) dialog.findViewById(R.id.contact_email_tv_detail)).setText(contact.getEmail());
        }

        ((TextView) dialog.findViewById(R.id.contact_name_tv_detail)).setText(contact.getName());


        Tools.addThumbnail(getActivity().getApplicationContext(), ((ImageView) dialog.findViewById(R.id.contact_image_iv_detail)), contact.getPhoto_Id());

        ((RelativeLayout) dialog.findViewById(R.id.back_rl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        ((RelativeLayout) dialog.findViewById(R.id.delete_rl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.remove(contact);
                adapter.notifyDataSetChanged();

                if(adapter.isEmpty())
                    relativeTwoWayView.setVisibility(View.GONE);

                dialog.dismiss();
            }
        });

        dialog.show();

        return dialog;
    }

}