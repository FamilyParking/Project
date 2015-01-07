package com.familyparking.app.utility;

import android.util.Log;

import com.familyparking.app.serverClass.Car;
import com.familyparking.app.serverClass.Result;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by francesco on 20/12/14.
 */
public class ServerCall {

    private static String service_add = Code.SERVER_PATH;

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
