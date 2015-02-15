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

public class Car implements Parcelable {

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("ID")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("brand")
    private String brand;

    @SerializedName("bluetoothName")
    private String bluetoothName;

    @SerializedName("bluetoothMac")
    private String bluetoothMac;

    public Car(){}

    public Car(String latitude, String longitude, String id, String name, String brand, String bluetoothName, String bluetoothMac) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.bluetoothName = bluetoothName;
        this.bluetoothMac = bluetoothMac;
    }

    public Car(String id, String name, String brand, String bluetoothName, String bluetoothMac) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.bluetoothName = bluetoothName;
        this.bluetoothMac = bluetoothMac;
    }

    public String[] getArray(){
        return new String[]{id,name,brand,bluetoothName,bluetoothMac};
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public String getBluetoothMac() {
        return bluetoothMac;
    }

    public void setBluetoothMac(String bluetoothMac) {
        this.bluetoothMac = bluetoothMac;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.latitude,this.longitude,this.id,this.name,this.brand});
    }

    public Car(Parcel in){
        String[] data = new String[5];
        in.readStringArray(data);

        this.latitude = data[0];
        this.longitude = data[1];
        this.id = data[2];
        this.name = data[3];
        this.brand = data[4];
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Car createFromParcel(Parcel in) {
            return new Car(in);
        }

        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

    public String toString(){
        return "[CAR]: "+id+"-"+name+"-"+brand;
    }
}