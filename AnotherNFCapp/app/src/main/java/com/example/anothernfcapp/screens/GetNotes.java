package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.caching.CacheForGetNotes;
import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.json.get_notes.JsonForGetNotesResponse;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class GetNotes extends Activity {
    private AsyncHttpClient asyncHttpClient;
    private TextView textView;
    private ImageButton goBackButton;
    private CacheForGetNotes cacheForGetNotes;
    private StringBuilder stringToCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_screen);
        cacheForGetNotes = new CacheForGetNotes(this);
        try {
            getJsonMessageFromServer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        textView = findViewById(R.id.valueFromServer);
        goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(v -> backButton());
        stringToCache = new StringBuilder();
        Log.d("CACHEGET", "Starting caching");
        String message;
        try {
            Log.d("CACHEGET", "Caching");
            message = cacheForGetNotes.getCachedNotes();
            Log.d("CACHEGET", message);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        textView.append(message);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    private void backButton() {
        Intent getScreen = new Intent(this, MainScreen.class);
        startActivity(getScreen);
    }

    private void getJsonMessageFromServer() throws UnsupportedEncodingException {
        asyncHttpClient = new AsyncHttpClient();
        String urlToGet = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "/notes";
        Log.d("GET", urlToGet);
        asyncHttpClient.get(urlToGet, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("GET", "Failed to connect to the server " + statusCode + " Response: " + responseString);
                BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, GetNotes.this);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("GET", "Successfully connected to the server");
                JsonForGetNotesResponse[] message;
                message = JsonFactory.makeStringForGetNotesResponse(responseString);
                Log.d("CACHEGET", "onSuccess: " + message);
                textView.setText("");
                stringToCache.append("Cached notes:");
                stringToCache.append("\n");
                for (JsonForGetNotesResponse msg:message) {
                    textView.append(msg.toString());
                    textView.append("\n");
                    stringToCache.append(msg);
                    stringToCache.append("\n");

                }
                try {
                    setCachedText();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setCachedText() throws IOException {
        Log.d("CACHEGET", stringToCache.toString());
        cacheForGetNotes.writeToCache(stringToCache.toString());
    }

}
