package it.familiyparking.app.dialog;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.serverClass.Contact;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class ContactDetailDialog extends Fragment{

    private Contact contact;

    public ContactDetailDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_detail_dialog, container, false);

        Tools.resetUpButtonActionBar((ActionBarActivity) getActivity());
        ((MainActivity) getActivity()).resetMenu();

        if(contact.getEmail().equals(contact.getName())){
            (rootView.findViewById(R.id.contact_email_tv_detail)).setVisibility(View.GONE);
        }
        else{
            ((TextView) rootView.findViewById(R.id.contact_email_tv_detail)).setText(contact.getEmail());
        }

        ((TextView) rootView.findViewById(R.id.contact_name_tv_detail)).setText(contact.getName());


        Tools.addThumbnail(getActivity().getApplicationContext(), ((ImageView) rootView.findViewById(R.id.contact_image_iv_detail)), contact.getPhoto_Id());

        (rootView.findViewById(R.id.back_rl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.contact = args.getParcelable("contact");
    }

    public void closeDialog(){
        MainActivity activity = (MainActivity) getActivity();
        activity.setMenu();
        activity.resetContactDetailDialog();
        activity.getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}
