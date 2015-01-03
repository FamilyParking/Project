package com.familyparking.app.serverClass;

/**
 * Created by francesco on 02/01/15.
 */
public class Contact {

    private int id;
    private int photo_Id;
    private String email;
    private String name;
    private boolean hasPhoto;

    public Contact(int id, String name, String email, boolean hasPhoto, int photo_id) {
        this.id = id;
        this.photo_Id = photo_id;
        this.email = email;
        this.name = name;
        this.hasPhoto = hasPhoto;
    }

    public Contact(int id, String name, String email, int hasPhoto, int photo_id) {
        this.id = id;
        this.photo_Id = photo_id;
        this.email = email;
        this.name = name;
        if(hasPhoto == 0)
            this.hasPhoto = false;
        else
            this.hasPhoto = true;
    }

    public int getId() {
        return id;
    }

    public int getPhoto_Id() {
        return photo_Id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public boolean hasPhoto() {
        return hasPhoto;
    }

    public String[] getArray(){
        String[] result = new String[5];
        result[0] = Integer.toString(id);
        result[1] = name;
        result[2] = email;
        if(hasPhoto)
            result[3] = "1";
        else
            result[3] = "0";
        result[4] = Integer.toString(photo_Id);
        return result;
    }

    public String toString(){
        return "{"+name+"}"+email+"["+hasPhoto+"]";
    }
}
