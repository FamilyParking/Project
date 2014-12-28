package com.familyparking.app.utility;

import android.util.Log;

import com.familyparking.app.serverClass.Car;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by francesco on 20/12/14.
 */
public class ServerCall {

    private static String service_add = "http://first-vision-798.appspot.com/";

    public static String sendPosition(Car car){

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

            Log.e("ServerCall", EntityUtils.toString(httpResponse.getEntity()));

            return Integer.toString(httpResponse.getStatusLine().getStatusCode());

        } catch(Exception e){
            Log.e("Car", e.toString() + " - " + e.getLocalizedMessage());
        }

        return null;
    }
}
