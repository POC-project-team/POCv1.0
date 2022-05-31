package com.example.anothernfcapp.utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.anothernfcapp.json.JsonFactory;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class BadStatusCodeProcess {
    public static void parseBadStatusCode(int statusCode, String response, Context context){
        if (statusCode == 0){
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        }
        else if (statusCode >= 400){
            if (statusCode == 406 && response.equals("The token has expired")) {
                try {
                    reLogin(context);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (response.equals("")){
                Toast.makeText(context, "Error " + statusCode, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "Error: " + response, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void reLogin(Context context) throws UnsupportedEncodingException {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = StaticVariables.ipServerUrl + "auth";
        String msg = JsonFactory.makeJsonForAuthUserRequest(StaticVariables.login, StaticVariables.password);
        StringEntity stringEntity = new StringEntity(msg);
        asyncHttpClient.post(context, url, stringEntity, msg, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("LOGIN", "Failed to connect to the server " + statusCode + " Response: " + responseString);
                BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, context);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("LOGIN", "Successfully connected to the server");
                Log.d("LOGIN", responseString);
                StaticVariables.setJWT(JsonFactory.makeStringForAuthUserResponse(responseString).toString());
            }
        });
    }
}
