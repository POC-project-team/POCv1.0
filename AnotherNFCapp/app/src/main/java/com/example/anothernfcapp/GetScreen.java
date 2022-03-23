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

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class GetScreen extends Activity {
    AsyncHttpClient asyncHttpClient;
    private String serverUrl = "https://172.20.10.12:60494/";
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
        Intent getScreen = new Intent(this, MainActivity.class);
        startActivity(getScreen);
    }

    private void getJsonMessageFromServer() {
        asyncHttpClient = new AsyncHttpClient();
        String urlToGet = serverUrl + "1/0/getNotes";
        Log.d("GET", urlToGet);
        asyncHttpClient.get(urlToGet, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("GET", "onSuccess");
                textView.setText(responseBody.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("GET", "Can't connect to the server. Connecting to google");
                asyncHttpClient.get("https://www.google.com", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("GET", "Connected to Google");
                        textView.setText(responseBody.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("GET", "Status code " + statusCode);
                    }
                });
            }
        });

    }

}
