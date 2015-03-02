package it.familiyparking.app.utility;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;

import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;

/**
 * Created by francesco on 20/12/14.
 */
public class ServerCall {

    private static String server_address = Code.SERVER_PATH;

    public static Result signIn(User user){

        /*try {
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

        return null;*/

        sleep();
        return new Result(true,"DEBUG","Debug");
    }

    public static Result confirmation(User user){

        /*try {
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

        return null;*/

        sleep();
        return new Result(true,"DEBUG","Debug");
    }

    public static Result updateGoogleCode(User user){

        /*try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(service_add+"updateGoogleCode");

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

        return null;*/

        sleep();
        return new Result(true,"DEBUG","Debug");
    }

    public static Result getAllCar(User user){

        sleep();
        return new Result(true,new ArrayList<Car>(),"Debug");
    }

    public static Result createCar(User user, Car car){

        /*try {
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

        return null;*/

        sleep();
        return new Result(true,"1","Debug");
    }

    public static Result updateCar(User user,Car car){

        /*try {
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

        return null;*/

        sleep();
        return new Result(true,"DEBUG","Debug");
    }

    public static Result addCarUsers(User user,String carID, ArrayList<User> toRemove){

        /*try {
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

        return null;*/

        sleep();
        return new Result(true,"DEBUG","Debug");
    }

    public static Result removeCarUsers(User user,String carID, ArrayList<User> toRemove){

        /*try {
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

        return null;*/

        sleep();
        return new Result(true,"DEBUG","Debug");
    }

    public static Result removeCar(User user,Car car){

        /*try {
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

        return null;*/

        sleep();
        return new Result(true,"DEBUG","Debug");
    }

    private static void printReturn(InputStreamReader reader){
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(reader, writer);
        }
        catch (Exception e){

        }
        String theString = writer.toString();
        Log.e("Result from Server",theString);
    }

    private static void sleep(){
        try {
            Thread.sleep(1000);
        }
        catch (Exception e){
            Log.e("Sleep",e.getMessage());
        }
    }

}
