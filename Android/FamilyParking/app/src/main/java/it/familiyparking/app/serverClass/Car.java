package it.familiyparking.app.serverClass;

/**
 * Created by francesco on 20/12/14.
 */

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 25/03/14.
 */

public class Car implements Parcelable {

    @SerializedName("ID_car")
    private String id;

    @SerializedName("Name")
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
    private String last_driver_email;

    @SerializedName("Bluetooth_Name")
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
        this.last_driver_email = in.readString();
        this.bluetoothName = in.readString();
        this.bluetoothMac = in.readString();
        in.readTypedList(this.users, User.CREATOR);
    }

    public String[] getArray(){
        if((last_driver_email == null) || last_driver_email.equals("none"))
            return new String[]{id,name,brand,register,latitude,longitude,Boolean.toString(isParked),timestamp,null,bluetoothName,bluetoothMac};
        else
            return new String[]{id,name,brand,register,latitude,longitude,Boolean.toString(isParked),timestamp,last_driver_email,bluetoothName,bluetoothMac};
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
        out.writeString(this.last_driver_email);
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

    @Override
    public String toString() {
        return "Car{" +
                "users=" + users +
                ", bluetoothMac='" + bluetoothMac + '\'' +
                ", bluetoothName='" + bluetoothName + '\'' +
                ", last_driver_email=" + last_driver_email +
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
        if(brand.equals("no_brand"))
            return "add_car";

        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getRegister() {
        if((register == null) || (register.equals("")))
            return null;

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
        if((bluetoothName == null) || (bluetoothName.equals("")))
            return null;

        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public String getBluetoothMac() {
        if((bluetoothMac == null) || (bluetoothMac.equals("")))
            return null;

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

    public String getLast_driver() {
        return last_driver_email;
    }

    public void setLast_driver(String last_driver_email) {
        this.last_driver_email = last_driver_email;
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

    public User getLastDriverUser(Context context){
        if(this.last_driver_email == null)
            return null;

        for(User u : this.users){
            if(u.getEmail().equals(this.last_driver_email))
                return u;
        }

        User temp = new User(Tools.getName_byEmail(context,this.last_driver_email),this.last_driver_email);

        /*String photoID = Tools.getPhotoID_byEmail(context,this.last_driver_email);
        if(photoID != null){
            temp.setPhoto_ID(photoID);
            temp.setHas_photo(true);
        }*/

        return  temp;
    }

    public boolean equals(Car car) {
        if ((bluetoothMac == null) && (car.bluetoothMac != null)) return false;
        if ((bluetoothMac != null) && (car.bluetoothMac == null)) return false;
        if ((bluetoothMac != null) && (car.bluetoothMac != null) && (!bluetoothMac.equals(car.bluetoothMac))) return false;

        if ((bluetoothName == null) && (car.bluetoothName != null)) return false;
        if ((bluetoothName != null) && (car.bluetoothName == null)) return false;
        if ((bluetoothName != null) && (car.bluetoothName != null) && (!bluetoothName.equals(car.bluetoothName))) return false;

        if ((brand == null) && (car.brand != null)) return false;
        if ((brand != null) && (car.brand == null)) return false;
        if ((brand != null) && (car.brand != null) && (!brand.equals(car.brand))) return false;

        if (!id.equals(car.id)) return false;
        if (!name.equals(car.name)) return false;

        if ((register == null) && (car.register != null)) return false;
        if ((register != null) && (car.register == null)) return false;
        if ((register != null) && (car.register != null) && (!register.equals(car.register))) return false;

        return true;
    }

    public void merge(Car car){
        this.name = car.getName();
        this.brand = car.getBrand();
        this.register = car.getRegister();
        this.bluetoothName = car.getBluetoothName();
        this.bluetoothMac = car.getBluetoothMac();
        this.users = car.getUsers();
    }
}