package it.familiyparking.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dialog.ContactDetailDialog;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.fragment.GroupFragment;
import it.familiyparking.app.fragment.ManageGroup;
import it.familiyparking.app.serverClass.Car;
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

        setImageAndNameForGroup(convertView, group);

        setLayoutCar(convertView, group);

        setListGroup(convertView,group);

        setOnLongClick(convertView,group);

        setOnClick(convertView,group);

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

        new Thread(new DoRemoveGroup(activity,groupFragment,groupID)).start();
    }

    private void showContact(AdapterView<?> parent, Group group, int position){
        Contact contact = (Contact) parent.getItemAtPosition(position);

        ContactDetailDialog contactDetailDialog = new ContactDetailDialog();

        activity.setContactDetailDialog(contactDetailDialog);

        Bundle bundle = new Bundle();
        bundle.putParcelable("contact",contact);

        contactDetailDialog.setArguments(bundle);

        activity.getSupportFragmentManager().beginTransaction().add(R.id.container, contactDetailDialog).commit();
    }

    private void setImageAndNameForGroup(View convertView, Group group){
        TextView group_image = (TextView) convertView.findViewById(R.id.group_iv);
        Tools.setImageForGroup(activity,group_image,group);

        ((TextView) convertView.findViewById(R.id.group_name_tv)).setText(group.getName());
    }

    private void setListGroup(View convertView, final Group group){
        TwoWayView listGroup = ((TwoWayView)convertView.findViewById(R.id.group_list));
        CustomHorizontalAdapter customHorizontalAdapter = new CustomHorizontalAdapter(activity,group.getContacts());
        listGroup.setAdapter(customHorizontalAdapter);
        listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showContact(parent,group,position);
            }
        });
    }

    private void setOnLongClick(View convertView, Group group){
        RelativeLayout container_item = (RelativeLayout) convertView.findViewById(R.id.group_item_container);
        container_item.setOnLongClickListener(this);
        container_item.setContentDescription(group.getId());

        (convertView.findViewById(R.id.group_rl)).setOnLongClickListener(this);
        (convertView.findViewById(R.id.relative_car_group)).setOnLongClickListener(this);
        (convertView.findViewById(R.id.relative_name_group)).setOnLongClickListener(this);
    }

    private void setOnClickDelete(View convertView){
        convertView.findViewById(R.id.delete_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGroup(v.getRootView().findViewById(R.id.group_item_container).getContentDescription().toString());
            }
        });
    }

    private void setOnClickModify(View convertView, final Group group){
        convertView.findViewById(R.id.modify_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageGroup modifyGroup = new ManageGroup();

                Bundle bundle = new Bundle();
                bundle.putParcelable("group",group);

                modifyGroup.setArguments(bundle);

                activity.setModifyGroup(modifyGroup);

                activity.getSupportFragmentManager().beginTransaction().add(R.id.container, modifyGroup).commit();
            }
        });
    }

    private void setOnClickBack(View convertView){
        convertView.findViewById(R.id.back_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getRootView().findViewById(R.id.button_rl_group).setVisibility(View.GONE);
                v.getRootView().findViewById(R.id.line).setVisibility(View.GONE);
            }
        });
    }

    private void setOnClick(View convertView,Group group){
        setOnClickBack(convertView);

        setOnClickModify(convertView,group);

        setOnClickDelete(convertView);
    }

    private void setLayoutCar(View convertView,Group group){
        Car car = group.getCar();

        if(car == null) {
            convertView.findViewById(R.id.relative_car_group).setVisibility(View.GONE);
        }
        else{
            convertView.findViewById(R.id.relative_car_group).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.car_iv).setBackgroundDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier(car.getBrand(), "drawable", activity.getPackageName())));
            ((TextView) convertView.findViewById(R.id.car_name_tv)).setText(car.getName());
        }
    }
}