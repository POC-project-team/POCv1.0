package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

//import com.example.anothernfcapp.caching.CacheForSendingNotes;
import com.example.anothernfcapp.caching.CacheForPostNotes;
import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.R;
import com.example.anothernfcapp.json.add_notes.JsonForAddNoteRequest;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class AddNote extends Activity {
    private AsyncHttpClient asyncHttpClient;
    private EditText value;
    private String urlToPost;
    private Button sendValue;
    private ImageButton goBack;
    private CacheForPostNotes cacheForSendingNotes;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_screen);
        cacheForSendingNotes = new CacheForPostNotes(AddNote.this);
        asyncHttpClient = new AsyncHttpClient();
        sendValue = findViewById(R.id.sendValue);
        sendValue.setOnClickListener(v -> {
            try {
                postMessage(value.getText().toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        if (cacheForSendingNotes.getCacheFilePostNotes().length() > 0) {
            Toast.makeText(this, "You need to send cached notes. Open settings, there you will see 'send cached notes' button", Toast.LENGTH_LONG).show();
        }
        goBack = findViewById(R.id.goBackWriteScreen);
        goBack.setOnClickListener(v -> goBackButton());
        value = findViewById(R.id.getTextValue);
    }


    private void goBackButton() {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }


    private void postMessage(String text) throws UnsupportedEncodingException {
        urlToPost = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "/note";
        Log.d("POST", urlToPost);
        String msg = JsonFactory.makeJsonForAddNoteRequest(text);
        StringEntity stringEntity = new StringEntity(msg);
        asyncHttpClient.post(this, urlToPost, stringEntity, msg, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("POST", "Failed to connect to server. " + statusCode + " Response: " + responseString);
                if (statusCode == 0){
                    try {
                        cacheForSendingNotes.writeToCache(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    makeToastCache();
                    return;
                }
                BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, AddNote.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("POST", "onSuccess");
                makeToast();
            }
        });

    }

    private void makeToastCache(){
        Toast.makeText(this, "No internet connection. Added to cache", Toast.LENGTH_SHORT).show();

    }

    private void makeToast() {
        Toast.makeText(this, "Successfully wrote your message", Toast.LENGTH_SHORT).show();
    }
}
