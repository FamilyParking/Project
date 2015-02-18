package it.familiyparking.app.serverClass;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Created by francesco on 03/01/15.
 */
public class ResultArray {
    @SerializedName("flag")
    private boolean flag;

    @SerializedName("object")
    private Object[] object;

    @SerializedName("description")
    private String description;

    public ResultArray(boolean flag, Object[] object, String description) {
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

    public void setObject(Object[] object) {
        this.object = object;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString(){
        return Boolean.toString(flag)+"-"+description+"-"+ Arrays.toString(object);
    }
}
