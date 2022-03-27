package com.example.anothernfcapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class WriteScreen extends Activity {
    AsyncHttpClient asyncHttpClient;
    private String serverUrl = "http://172.20.10.12:60494/";
    EditText value;
    Button sendButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_screen);
        asyncHttpClient = new AsyncHttpClient();
        sendButton = (Button) findViewById(R.id.sendValue);
        value = findViewById(R.id.getTextValue);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    postMessage(value.getText().toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void postMessage(String text) throws UnsupportedEncodingException {
        JsonFactory jsonFactory = new JsonFactory();
        MainScreen mainScreen = new MainScreen();
        String msg = jsonFactory.makeJsonMessage(text, MainScreen.tagId);
        Log.d("POST", "postMessage: " + MainScreen.tagId);
        String urlToPost = serverUrl + "1/0/addNote";
        Log.d("POST", urlToPost);
        Log.d("POST", text);
        StringEntity stringEntity = new StringEntity(msg);
        asyncHttpClient.post(this, urlToPost, stringEntity, msg, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("POST", "Can't connect to the server. Connecting to google");
                Log.e("POST", "Status code: " + statusCode);
                asyncHttpClient.post("https://www.google.com", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("POST", "Connected to Google");
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("POST", "Status code: " + statusCode);
                    }
                });
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("POST", "onSuccess");
            }
        });
    }
}
