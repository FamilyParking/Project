package it.familiyparking.app.serverClass;

import java.util.ArrayList;

/**
 * Created by francesco on 25/03/14.
 */

public class Group {

    private String id;

    private String name;

    private Car car;

    private ArrayList<Contact> contacts;

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
        return contacts;
    }
}