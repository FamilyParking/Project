package com.familyparking.app.serverClass;

/**
 * Created by francesco on 02/01/15.
 */
public class Contact {

    private int id;
    private int photo_Id;
    private String email;
    private boolean hasPhoto;

    public Contact(int id, int photo_id, String email, boolean hasPhoto) {
        this.id = id;
        this.photo_Id = photo_id;
        this.email = email;
        this.hasPhoto = hasPhoto;
    }

    public Contact(int id, int photo_id, String email, int hasPhoto) {
        this.id = id;
        this.photo_Id = photo_id;
        this.email = email;
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

    public boolean hasPhoto() {
        return hasPhoto;
    }

    public String[] getArray(){
        String[] result = new String[4];
        result[0] = Integer.toString(id);
        result[1] = email;
        if(hasPhoto)
            result[2] = "1";
        else
            result[2] = "0";
        result[3] = Integer.toString(photo_Id);
        return result;
    }

    public String toString(){
        return email+"["+hasPhoto+"]";
    }
}
