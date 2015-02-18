package it.familiyparking.app.utility;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.CreateRelationGroupCar;
import it.familiyparking.app.serverClass.Group;
import it.familiyparking.app.serverClass.GroupForCall;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.ResultArray;
import it.familiyparking.app.serverClass.User;

/**
 * Created by francesco on 20/12/14.
 */
public class ServerCall {

    private static String service_add = Code.SERVER_PATH;

    public static Result signIn(User user){

        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(service_add+"registration");

            Gson gson = new Gson();
            String json = gson.toJson(user);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("signIn", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result confirmation(User user){

        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(service_add+"confirmCode");

            Gson gson = new Gson();
            String json = gson.toJson(user);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("confirmation", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result createCar(Car car){

        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(service_add+"createCar");

            Gson gson = new Gson();
            String json = gson.toJson(car);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            Log.e("createCar",json);

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("createCar", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result updatePosition(Car car){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(service_add+"updatePosition");

            Gson gson = new Gson();
            String json = gson.toJson(car);

            Log.e("UpdatePosition",json);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("updatePosition", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result createGroup(GroupForCall groupForCall){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(service_add+"createGroup");

            Gson gson = new Gson();
            String json = gson.toJson(groupForCall);

            Log.e("createGroup",json);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("createGroup", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result insertCarToGroup(CreateRelationGroupCar createRelationGroupCar){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(service_add+"insertCarGroup");

            Gson gson = new Gson();
            String json = gson.toJson(createRelationGroupCar);

            Log.e("insertCar",json);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("insertCarToGroup", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result getAllCarCreatedByMe(User user){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(service_add+"getAllCars_fromEmail");

            Gson gson = new Gson();
            String json = gson.toJson(user);

            Log.e("getAllCarCreatedByMe",json);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            if(result.isFlag()){
                ArrayList<Car> list = new ArrayList<>();

                JSONArray jsonArray = new JSONArray(gson.toJson(result.getObject()));
                for(int i=0; i < jsonArray.length(); i++){
                    list.add(gson.fromJson(jsonArray.getJSONObject(i).toString(), Car.class));
                }

                result.setObject(list);

                for(Car car : list){
                    Log.e("GetAllCarCreatedByMe",car.toString());
                }
            }
            else {
                JSONObject jsonObj = new JSONObject(gson.toJson(result.getObject()));
                result.setObject(jsonObj.getString("$"));
            }

            return result;

        } catch(Exception e){
            Log.e("getAllCarCreatedByMe", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result removeContactFromGroup(GroupForCall groupForCall){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(service_add+"removeContactGroup");

            Gson gson = new Gson();
            String json = gson.toJson(groupForCall);

            Log.e("removeContactFromGroup",json);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("removeContactFromGroup", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result addContactFromGroup(GroupForCall groupForCall){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(service_add+"insertContactGroup");

            Gson gson = new Gson();
            String json = gson.toJson(groupForCall);

            Log.e("addContactFromGroup",json);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("addContactFromGroup", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result updateGroupName(GroupForCall groupForCall){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(service_add+"editGroup");

            Gson gson = new Gson();
            String json = gson.toJson(groupForCall);

            Log.e("updateGroupName",json);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("updateGroupName", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result deleteGroup(GroupForCall groupForCall){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(service_add+"deleteGroup");

            Gson gson = new Gson();
            String json = gson.toJson(groupForCall);

            Log.e("deleteGroup",json);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("deleteGroup", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result deleteCar(Car car){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(service_add+"deleteCar");

            Gson gson = new Gson();
            String json = gson.toJson(car);

            Log.e("deleteGroup",json);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("deleteCar", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result modifyCar(Car car){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(service_add+"editCar");

            Gson gson = new Gson();
            String json = gson.toJson(car);

            Log.e("modifyCar",json);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("modifyCar", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    private void printReturn(InputStreamReader reader){
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(reader, writer);
        }
        catch (Exception e){

        }
        String theString = writer.toString();
        Log.e("Result from Server",theString);
    }


}
