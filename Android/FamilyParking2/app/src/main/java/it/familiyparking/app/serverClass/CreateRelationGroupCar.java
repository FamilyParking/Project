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

public class CreateRelationGroupCar{

    @SerializedName("Email")
    private String email;

    @SerializedName("Code")
    private String code;

    @SerializedName("ID_car")
    private String id_car;

    @SerializedName("ID_group")
    private String id_group;

    public CreateRelationGroupCar(String email, String code, String id_car, String id_group) {
        this.code = code;
        this.email = email;
        this.id_car = id_car;
        this.id_group = id_group;
    }

    public String getId_car() {
        return id_car;
    }

    public void setId_car(String id_car) {
        this.id_car = id_car;
    }

    public String getId_group() {
        return id_group;
    }

    public void setId_group(String id_group) {
        this.id_group = id_group;
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
}