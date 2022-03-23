package com.example.anothernfcapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class GetScreen extends Activity {
    AsyncHttpClient asyncHttpClient;
    private String serverUrl = "http://172.20.10.12:60494/";
    TextView textView;
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_screen);
        getJsonMessageFromServer();
        textView = (TextView)findViewById(R.id.valueFromServer);
        button = (Button) findViewById(R.id.goBackButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton();
            }
        });
    }

    private void backButton(){
        Intent getScreen = new Intent(this, MainScreen.class);
        startActivity(getScreen);
    }

    private void getJsonMessageFromServer() {
        asyncHttpClient = new AsyncHttpClient();
        String urlToGet = serverUrl + "1/0/getNotes";
        Log.d("GET", urlToGet);
        /*
        asyncHttpClient.get(urlToGet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String res) {
                Log.d("GET", "onSuccess");
                textView.setText(res);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable error) {
                Log.d("GET", "Can't connect to the server. Connecting to google");
                asyncHttpClient.get("https://www.google.com", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Log.d("GET", "Connected to Google");
                        textView.setText(res);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable error) {
                        Log.e("GET", "Status code " + statusCode);
                    }
                });
            }
        });
        */
        asyncHttpClient.get(urlToGet, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("GET", "onSuccess");
                textView.setText(responseString);
            }
        });

    }

}
