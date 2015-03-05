package it.familiyparking.app.dialog;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class ContactDetailDialog extends Fragment{

    private User contact;
    private User user;

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

        ImageView photo = (ImageView) rootView.findViewById(R.id.contact_image_iv_detail);
        TextView textView = (TextView) rootView.findViewById(R.id.contact_image_tv_detail);

        Tools.addThumbnail(getActivity(), photo, textView, contact);

        if(contact.equals(user)) {
            rootView.findViewById(R.id.button_rl_detail_all).setVisibility(View.GONE);
            rootView.findViewById(R.id.button_rl_detail_only_view).setVisibility(View.VISIBLE);

            (rootView.findViewById(R.id.back_rl)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDialog();
                }
            });
        }
        else {
            rootView.findViewById(R.id.button_rl_detail_all).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.button_rl_detail_only_view).setVisibility(View.GONE);

            (rootView.findViewById(R.id.back_all_rl)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDialog();
                }
            });

            (rootView.findViewById(R.id.delete_rl)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteContact();
                }
            });
        }

        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.user = args.getParcelable("user");
        this.contact = args.getParcelable("contact");
    }

    public void closeDialog(){
        final MainActivity activity = (MainActivity) getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.resetContactDetailDialog();
            }
        });
    }

    public void deleteContact(){
        final MainActivity activity = (MainActivity) getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.removeContact(contact);
                activity.resetContactDetailDialog();
            }
        });
    }

}
