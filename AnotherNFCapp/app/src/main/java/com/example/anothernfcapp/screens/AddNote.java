package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.R;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class AddNote extends Activity {
    AsyncHttpClient asyncHttpClient;
    EditText value;
    Button sendButton;
    Button backButton;
    String urlToPost;
    BadStatusCodeProcess badStatusCodeProcess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_screen);
        badStatusCodeProcess = new BadStatusCodeProcess();
        asyncHttpClient = new AsyncHttpClient();
        sendButton = (Button) findViewById(R.id.sendValue);
        backButton = findViewById(R.id.goBackWriteScreen);
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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackButton();
            }
        });

    }

    private void goBackButton() {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }


    private void postMessage(String text) throws UnsupportedEncodingException {
        urlToPost = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "/note";
        Log.d("POST", urlToPost);
        JsonFactory jsonFactory = new JsonFactory();
        String msg = jsonFactory.makeJsonForAddNoteRequest(text);
        StringEntity stringEntity = new StringEntity(msg);
        asyncHttpClient.post(this, urlToPost, stringEntity, msg, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("POST", "Failed to connect to server. " + statusCode + " Response: " + responseString);
                badStatusCodeProcess.parseBadStatusCode(statusCode, responseString, AddNote.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("POST", "onSuccess");
                makeToast();
            }
        });

    }

    private void makeToast() {
        Toast.makeText(this, "Successfully wrote your message", Toast.LENGTH_SHORT).show();
    }
}
