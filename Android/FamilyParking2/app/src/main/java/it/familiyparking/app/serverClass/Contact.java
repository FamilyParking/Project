package it.familiyparking.app.serverClass;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by francesco on 02/01/15.
 */
public class Contact implements Parcelable {

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

    public Contact(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);
        this.id = Integer.getInteger(data[0]).intValue();
        this.photo_Id = Integer.getInteger(data[1]).intValue();
        this.email = data[2];
        this.name = data[3];
        this.hasPhoto = Boolean.getBoolean(data[4]);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {Integer.toString(this.id),Integer.toString(this.photo_Id),this.email,this.name,Boolean.toString(hasPhoto)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public boolean equals(Contact c){
        return (c.getId() == this.id);
    }
}
