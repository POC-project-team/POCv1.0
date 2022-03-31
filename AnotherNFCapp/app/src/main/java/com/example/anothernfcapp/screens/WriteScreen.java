package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class WriteScreen extends Activity {
    AsyncHttpClient asyncHttpClient;
    EditText value;
    Button sendButton;
    SharedPreferences sharedPreferences;
    String urlToPost;
    String typeOfConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WriteScreen.this);
        typeOfConnection = sharedPreferences.getString(getString(R.string.connection_key), "wi-fi");
        Log.e("POST", typeOfConnection);


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
        if (typeOfConnection.equals("wi-fi")){
            urlToPost = MainScreen.ipServerUrl + "1/addNote";
            Log.e("POST", urlToPost);
        }
        else if (typeOfConnection.equals("localhost")){
            urlToPost = MainScreen.localhostUrl + "1/addNote";
            Log.e("POST", urlToPost);
        }
        JsonFactory jsonFactory = new JsonFactory();
        String msg = jsonFactory.makeJsonForAddNoteRequest(text, MainScreen.tagId);
        Log.d("POST", "postMessage: " + MainScreen.tagId);
        Log.d("POST", urlToPost);
        Log.d("POST", text);
        StringEntity stringEntity = new StringEntity(msg);
        asyncHttpClient.post(this, urlToPost, stringEntity, msg, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                toastOnSuccess(statusCode);
                Log.d("POST", "Can't connect to the server. Connecting to google");
                Log.e("POST", "Status code: " + statusCode);
                asyncHttpClient.post("https://www.google.com", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        toastOnSuccess(statusCode);
                        Log.d("POST", "Connected to Google");
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        toastOnSuccess(statusCode);
                        Log.e("POST", "Status code: " + statusCode);
                    }
                });
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                toastOnSuccess(statusCode);
                Log.d("POST", "onSuccess");
            }
        });
    }

    private void toastOnSuccess(int code) {
        if (code >= 200 && code < 400){
            Toast.makeText(this, "Successfully wrote", Toast.LENGTH_SHORT).show();
        }
        else if (code>=400){
            Toast.makeText(this, "Error while writing", Toast.LENGTH_SHORT).show();
        }
    }

}
