package it.familiyparking.app.serverClass;

/**
 * Created by francesco on 20/12/14.
 */

import com.google.gson.annotations.SerializedName;

/**
 * Created by francesco on 25/03/14.
 */

public class User {

    @SerializedName("Code")
    private String code;

    @SerializedName("Nickname")
    private String name;

    @SerializedName("Email")
    private String email;

    @SerializedName("ID")
    private String device_ID;

    private boolean has_photo;

    private String photo_id;

    private boolean ghostmode;

    public User(String name, String email, String device_ID) {
        this.name = name;
        this.email = email;
        this.device_ID = device_ID;
        this.ghostmode = false;
    }

    public User(String code, String name, String email, String device_ID, boolean has_photo, String photo_id, boolean ghostmode) {
        this.code = code;
        this.name = name;
        this.email = email;
        this.device_ID = device_ID;
        this.has_photo = has_photo;
        this.photo_id = photo_id;
        this.ghostmode = ghostmode;
    }

    public String getDevice_ID() {
        return device_ID;
    }

    public void setDevice_ID(String device_ID) {
        this.device_ID = device_ID;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHas_photo() {
        return has_photo;
    }

    public void setHas_photo(boolean has_photo) {
        this.has_photo = has_photo;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public boolean isGhostmode() {
        return ghostmode;
    }

    public void setGhostmode(boolean ghostmode) {
        this.ghostmode = ghostmode;
    }

    public String[] getArray(){
        String[] array = new String[7];
        array[0] = this.code;
        array[1] = this.name;
        array[2] = this.email;
        array[3] = this.device_ID;
        array[4] = Boolean.toString(this.has_photo);
        array[5] = this.photo_id;
        array[6] = Boolean.toString(this.ghostmode);
        return array;
    }
}