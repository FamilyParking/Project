package it.familiyparking.app.serverClass;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by francesco on 03/01/15.
 */
public class Result {
    @SerializedName("Flag")
    private boolean flag;

    @SerializedName("Object")
    private Object object;

    @SerializedName("Description")
    private String description;

    public Result(){}

    public Result(boolean flag, Object object, String description) {
        this.flag = flag;
        this.object = object;
        this.description = description;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setObject(ArrayList object) {
        this.object = object;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString(){
        return Boolean.toString(flag)+"-"+description+"-"+object.toString();
    }
}
