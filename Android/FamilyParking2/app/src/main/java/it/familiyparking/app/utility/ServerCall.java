package it.familiyparking.app.utility;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
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
            Log.e("sendPosition", e.toString() + " - " + e.getLocalizedMessage());
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
            Log.e("sendPosition", e.toString() + " - " + e.getLocalizedMessage());
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
            Log.e("sendPosition", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }

    public static Result sendPosition(Car car){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(service_add+"sign");

            Gson gson = new Gson();
            String json = gson.toJson(car);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());

            Result result = gson.fromJson(reader,Result.class);

            return result;

        } catch(Exception e){
            Log.e("sendPosition", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }
}
