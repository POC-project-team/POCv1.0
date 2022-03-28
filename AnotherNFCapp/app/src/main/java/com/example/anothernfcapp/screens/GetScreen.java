package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.anothernfcapp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

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
        asyncHttpClient.get(urlToGet, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("GET", "Failure to connect to the server");
                asyncHttpClient.get("https://google.com", new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("GET", "Connection error. Status code: " + statusCode);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d("GET", "Connected to Google");
                    }
                });

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("GET", "onSuccess");
                textView.setText(responseString);
            }
        });

    }

}
