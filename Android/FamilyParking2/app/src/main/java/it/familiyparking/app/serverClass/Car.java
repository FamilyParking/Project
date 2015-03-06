package it.familiyparking.app.serverClass;

/**
 * Created by francesco on 20/12/14.
 */
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by francesco on 25/03/14.
 */

public class Car implements Parcelable {

    @SerializedName("ID_car")
    private String id;

    @SerializedName("Name_car")
    private String name;

    @SerializedName("Brand")
    private String brand;

    @SerializedName("Register")
    private String register;

    @SerializedName("Latitude")
    private String latitude;

    @SerializedName("Longitude")
    private String longitude;

    @SerializedName("isParked")
    private boolean isParked;

    @SerializedName("Timestamp")
    private String timestamp;

    @SerializedName("Last_driver")
    private User last_driver;

    @SerializedName("Bluetooth_name")
    private String bluetoothName;

    @SerializedName("Bluetooth_MAC")
    private String bluetoothMac;

    @SerializedName("Users")
    private ArrayList<User> users;

    public Car(){}

    public Car(String id, String name, String brand, String register, String latitude, String longitude, boolean isParked, String timestamp, String bluetoothName, String bluetoothMac){
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.register = register;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isParked = isParked;
        this.timestamp = timestamp;
        this.bluetoothName = bluetoothName;
        this.bluetoothMac = bluetoothMac;
    }

    private Car(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.brand = in.readString();
        this.register = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.isParked = Boolean.parseBoolean(in.readString());
        this.timestamp = in.readString();
        this.last_driver = in.readParcelable(User.class.getClassLoader());
        this.bluetoothName = in.readString();
        this.bluetoothMac = in.readString();
        in.readTypedList(this.users, User.CREATOR);
    }

    public String[] getArray(){
        if(last_driver == null)
            return new String[]{id,name,brand,register,latitude,longitude,Boolean.toString(isParked),timestamp,null,bluetoothName,bluetoothMac};
        else
            return new String[]{id,name,brand,register,latitude,longitude,Boolean.toString(isParked),timestamp,last_driver.getEmail(),bluetoothName,bluetoothMac};
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.id);
        out.writeString(this.name);
        out.writeString(this.brand);
        out.writeString(this.register);
        out.writeString(this.latitude);
        out.writeString(this.longitude);
        out.writeString(Boolean.toString(this.isParked));
        out.writeString(this.timestamp);
        out.writeParcelable(this.last_driver,flags);
        out.writeString(this.bluetoothName);
        out.writeString(this.bluetoothMac);
        out.writeTypedList(this.users);
    }

    public static final Parcelable.Creator<Car> CREATOR = new Parcelable.Creator<Car>() {
        public Car createFromParcel(Parcel in) {
            return new Car(in);
        }

        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

    public boolean equals(Car car){
        return this.id.equals(car.getId());
    }

    @Override
    public String toString() {
        return "Car{" +
                "users=" + users +
                ", bluetoothMac='" + bluetoothMac + '\'' +
                ", bluetoothName='" + bluetoothName + '\'' +
                ", last_driver=" + last_driver +
                ", timestamp='" + timestamp + '\'' +
                ", isParked=" + isParked +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", register='" + register + '\'' +
                ", brand='" + brand + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean isParked() {
        return isParked;
    }

    public void setParked(boolean isParked) {
        this.isParked = isParked;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public User getLast_driver() {
        return last_driver;
    }

    public void setLast_driver(User last_driver) {
        this.last_driver = last_driver;
    }

    public Car clone(){
        Car newCar = new Car();

        newCar.setId(this.getId());
        newCar.setName(this.getName());
        newCar.setBrand(this.getBrand());
        newCar.setRegister(this.getRegister());
        newCar.setLatitude(this.getLatitude());
        newCar.setLongitude(this.getLongitude());
        newCar.setParked(this.isParked());
        newCar.setTimestamp(this.getTimestamp());
        newCar.setLast_driver(this.getLast_driver());
        newCar.setBluetoothName(this.getBluetoothName());
        newCar.setBluetoothMac(this.getBluetoothMac());

        ArrayList<User> list = new ArrayList<>();
        for(User contact : users)
            list.add(contact);

        newCar.setUsers(list);

        return newCar;
    }

    public void setPosition(double[] position){
        setLatitude(Double.toString(position[0]));
        setLongitude(Double.toString(position[1]));
    }
}