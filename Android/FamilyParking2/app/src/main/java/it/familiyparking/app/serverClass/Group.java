package it.familiyparking.app.serverClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francesco on 25/03/14.
 */

public class Group implements Parcelable {

    private String id;

    private String name;

    private Car car;

    private List<Contact> contacts;

    public Group(){}

    public Group(String id, String name, Car car, ArrayList<Contact> contacts) {
        this.id = id;
        this.name = name;
        this.car = car;
        this.contacts = contacts;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Car getCar() {
        return car;
    }

    public ArrayList<Contact> getContacts() {
        return (ArrayList<Contact>) contacts;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(car, flags);
        out.writeString(id);
        out.writeString(name);
        out.writeTypedList(contacts);
    }

    private Group(Parcel in) {
        car = (Car) in.readParcelable(Car.class.getClassLoader());
        id = in.readString();
        name = in.readString();
        in.readTypedList(contacts, Contact.CREATOR);
    }

    public int describeContents() {
        return this.hashCode();
    }

    public static final Parcelable.Creator<Group> CREATOR =
            new Parcelable.Creator<Group>() {
                public Group createFromParcel(Parcel in) {
                    return new Group(in);
                }

                public Group[] newArray(int size) {
                    return new Group[size];
                }
            };

    public String toString(){
        String result = "[GROUP]: "+id+"-"+name+"-"+car.toString()+"\n";

        for(Contact c : contacts)
            result = result + c.toString() + "\n";

        return result;
    }
}