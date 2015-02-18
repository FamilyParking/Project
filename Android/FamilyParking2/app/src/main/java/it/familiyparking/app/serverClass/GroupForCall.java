package it.familiyparking.app.serverClass;

/**
 * Created by francesco on 20/12/14.
 */
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by francesco on 25/03/14.
 */

public class GroupForCall{

    @SerializedName("Email")
    private String email;

    @SerializedName("Code")
    private String code;

    @SerializedName("Name")
    private String name;

    @SerializedName("ID_group")
    private String ID_group;

    @SerializedName("List_email")
    private String[] contacts;

    public GroupForCall(String email, String code, String name, ArrayList<Contact> contactArrayList) {
        this.email = email;
        this.code = code;
        this.name = name;
        setContacts(contactArrayList);
    }

    public GroupForCall(String id, String email, String code, String name, ArrayList<Contact> contactArrayList) {
        this.ID_group = id;
        this.email = email;
        this.code = code;
        this.name = name;
        if(contactArrayList != null)
            setContacts(contactArrayList);
    }

    public GroupForCall(String id, String email, String code) {
        this.ID_group = id;
        this.email = email;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contactArrayList) {
        this.contacts = new String[contactArrayList.size()];

        int i=0;
        for(Contact c : contactArrayList) {
            this.contacts[i] = c.getEmail();
            i++;
        }

    }
}