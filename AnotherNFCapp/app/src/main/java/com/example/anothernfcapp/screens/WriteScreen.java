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
        Log.d("POST", typeOfConnection);
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
        }
        else if (typeOfConnection.equals("localhost")){
            urlToPost = MainScreen.localhostUrl + "1/addNote";
        }
        Log.d("POST", urlToPost);
        JsonFactory jsonFactory = new JsonFactory();
        String msg = jsonFactory.makeJsonForAddNoteRequest(text, MainScreen.tagId);
        StringEntity stringEntity = new StringEntity(msg);
        asyncHttpClient.post(this, urlToPost, stringEntity, msg, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onPostRequest(statusCode);
                Log.e("POST", "onFailure. Status code " + statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                onPostRequest(statusCode);
                Log.d("POST", "onSuccess");
            }
        });
    }

    private void onPostRequest(int code) {
        if (code >= 200 && code < 400){
            Toast.makeText(this, "Successfully wrote to the server", Toast.LENGTH_SHORT).show();
        }
        else if (code>=400){
            Toast.makeText(this, "Error while writing to the server. Status code: " + code, Toast.LENGTH_SHORT).show();
        }
    }

}
