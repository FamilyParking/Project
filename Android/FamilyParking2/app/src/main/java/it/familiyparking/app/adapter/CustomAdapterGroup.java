package it.familiyparking.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dialog.ContactDetailDialog;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.fragment.GroupFragment;
import it.familiyparking.app.serverClass.Contact;
import it.familiyparking.app.serverClass.Group;
import it.familiyparking.app.task.DoRemoveGroup;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomAdapterGroup extends ArrayAdapter<Group> implements View.OnLongClickListener{

    private MainActivity activity;
    private ArrayList<Group> groups;
    private GroupFragment groupFragment;

    public CustomAdapterGroup(Fragment fragment, Context context, ArrayList<Group> groups, Activity activity) {
        super(context, 0, groups);
        this.activity = (MainActivity)activity;
        this.groups = groups;
        this.groupFragment = (GroupFragment) fragment;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Group group = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_item, parent, false);
        }

        TextView group_image = (TextView) convertView.findViewById(R.id.group_iv);
        Drawable drawable = activity.getResources().getDrawable(R.drawable.circle);
        drawable.setColorFilter(new PorterDuffColorFilter(Tools.randomColor(activity),PorterDuff.Mode.SCREEN));
        group_image.setBackgroundDrawable(drawable);
        String initial = ""+group.getName().charAt(0)+"";
        group_image.setText(initial.toUpperCase());

        ((TextView) convertView.findViewById(R.id.group_name_tv)).setText(group.getName());
        ((TextView) convertView.findViewById(R.id.car_name_tv)).setText(group.getCar().getName());

        TwoWayView listGroup = ((TwoWayView)convertView.findViewById(R.id.group_list));
        CustomHorizontalAdapter customHorizontalAdapter = new CustomHorizontalAdapter(activity,group.getContacts());
        listGroup.setAdapter(customHorizontalAdapter);
        listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = (Contact) parent.getItemAtPosition(position);

                ContactDetailDialog contactDetailDialog = new ContactDetailDialog();

                activity.setContactDetailDialog(contactDetailDialog);

                Bundle bundle = new Bundle();
                bundle.putParcelable("contact",contact);
                bundle.putString("groupID",group.getId());

                contactDetailDialog.setArguments(bundle);

                activity.getSupportFragmentManager().beginTransaction().add(R.id.container, contactDetailDialog).commit();
            }
        });

        RelativeLayout container_item = (RelativeLayout) convertView.findViewById(R.id.group_item_container);
        container_item.setOnLongClickListener(this);
        container_item.setContentDescription(group.getId());

        (convertView.findViewById(R.id.group_rl)).setOnLongClickListener(this);
        (convertView.findViewById(R.id.relative_car_group)).setOnLongClickListener(this);
        (convertView.findViewById(R.id.relative_name_group)).setOnLongClickListener(this);

        convertView.findViewById(R.id.back_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getRootView().findViewById(R.id.button_rl_group).setVisibility(View.GONE);
                v.getRootView().findViewById(R.id.line).setVisibility(View.GONE);
            }
        });

        convertView.findViewById(R.id.modify_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        convertView.findViewById(R.id.delete_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGroup(v.getRootView().findViewById(R.id.group_item_container).getContentDescription().toString());
            }
        });

        return convertView;
    }

    @Override
    public boolean onLongClick(View v) {
        RelativeLayout containerButton = (RelativeLayout) v.getRootView().findViewById(R.id.button_rl_group);
        View line = v.getRootView().findViewById(R.id.line);

        if(!containerButton.isShown()){
            containerButton.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);

            return true;
        }

        return false;
    }

    private void deleteGroup(String groupID){
        ProgressDialogCircular progressDialog = new ProgressDialogCircular();
        activity.setProgressDialogCircular(progressDialog);

        Bundle bundle = new Bundle();
        bundle.putString("message", "Deleting group ...");
        progressDialog.setArguments(bundle);

        activity.getSupportFragmentManager().beginTransaction().add(R.id.container, progressDialog).commit();

        new Thread(new DoRemoveGroup(activity,groupFragment,groupID,groups,progressDialog)).start();
    }


}