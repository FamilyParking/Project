package it.familiyparking.app.serverClass;

/**
 * Created by francesco on 20/12/14.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by francesco on 25/03/14.
 */

public class User  implements Parcelable {

    @SerializedName("Nickname")
    private String name;

    @SerializedName("Email")
    private String email;

    @SerializedName("Code")
    private String code;

    @SerializedName("ID_gcm")
    private String google_cloud_messaging;

    private boolean has_photo;

    private String photo_id;

    private boolean ghostmode;

    public User(){}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.ghostmode = false;
    }

    public User(String name, String email, boolean has_photo, String photo_id) {
        this.name = name;
        this.email = email;
        this.has_photo = has_photo;
        this.photo_id = photo_id;
    }

    public User(String name, String email, String gcm) {
        this.name = name;
        this.email = email;
        this.google_cloud_messaging = gcm;
        this.ghostmode = false;
    }

    public User(String code, String name, String email, String gcm, boolean has_photo, String photo_id, boolean ghostmode) {
        this.code = code;
        this.name = name;
        this.email = email;
        this.google_cloud_messaging = gcm;
        this.has_photo = has_photo;
        this.photo_id = photo_id;
        this.ghostmode = ghostmode;
    }

    public User(Parcel in){
        String[] data = new String[7];
        in.readStringArray(data);

        this.code = data[0];
        this.name = data[1];
        this.email = data[2];
        this.google_cloud_messaging = data[3];
        this.has_photo = Boolean.parseBoolean(data[4]);
        this.photo_id = data[5];
        this.ghostmode = Boolean.parseBoolean(data[6]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.code,
                this.name,
                this.email,
                this.google_cloud_messaging,
                Boolean.toString(this.has_photo),
                this.photo_id,
                Boolean.toString(this.ghostmode)
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String[] getArray(){
        String[] array = new String[7];
        array[0] = this.code;
        array[1] = this.name;
        array[2] = this.email;
        array[3] = this.google_cloud_messaging;
        array[4] = Boolean.toString(this.has_photo);
        array[5] = this.photo_id;
        array[6] = Boolean.toString(this.ghostmode);
        return array;
    }

    public String[] getContactArray(){
        String[] array = new String[4];
        array[0] = this.name;
        array[1] = this.email;
        array[2] = Boolean.toString(this.has_photo);
        array[3] = this.photo_id;

        return array;
    }

    public boolean equals(User user){
        return this.email.equals(user.getEmail());
    }

    @Override
    public String toString() {
        if(code != null) {
            return "User{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", code='" + code + '\'' +
                    ", google_cloud_messaging='" + google_cloud_messaging + '\'' +
                    ", has_photo=" + has_photo +
                    ", photo_id='" + photo_id + '\'' +
                    ", ghostmode=" + ghostmode +
                    '}';
        }
        else{
            return "User{name='" + name + '\'' + ", email='" + email + '}';
        }
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGoogle_cloud_messaging() {
        return google_cloud_messaging;
    }

    public void setGoogle_cloud_messaging(String google_cloud_messaging) {
        this.google_cloud_messaging = google_cloud_messaging;
    }

    public boolean has_photo() {
        return has_photo;
    }

    public void setHas_photo(boolean has_photo) {
        this.has_photo = has_photo;
    }

    public String getPhoto_ID() {
        return photo_id;
    }

    public void setPhoto_ID(String photo_id) {
        this.photo_id = photo_id;
    }

    public boolean isGhostmode() {
        return ghostmode;
    }

    public void setGhostmode(boolean ghostmode) {
        this.ghostmode = ghostmode;
    }


}