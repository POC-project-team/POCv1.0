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
import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.json.get_notes.JsonForGetNotesResponse;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class GetNotes extends Activity {
    AsyncHttpClient asyncHttpClient;
    TextView textView;
    Button button;
    JsonFactory jsonFactory;
    BadStatusCodeProcess badStatusCodeProcess;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_screen);
        badStatusCodeProcess = new BadStatusCodeProcess();
        try {
            getJsonMessageFromServer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        textView = (TextView) findViewById(R.id.valueFromServer);
        button = (Button) findViewById(R.id.goBackButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton();
            }
        });
    }

    private void backButton() {
        Intent getScreen = new Intent(this, MainScreen.class);
        startActivity(getScreen);
    }

    private void getJsonMessageFromServer() throws UnsupportedEncodingException {
        asyncHttpClient = new AsyncHttpClient();
        String urlToGet = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "/notes";
        Log.d("GET", urlToGet);
        jsonFactory = new JsonFactory();
        asyncHttpClient.get(urlToGet, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("GET", "Failed to connect to the server "  + statusCode + " Response: " + responseString);
                badStatusCodeProcess.parseBadStatusCode(statusCode, responseString, GetNotes.this);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("GET", "Successfully connected to the server");
                JsonForGetNotesResponse[] message;
                message = jsonFactory.makeStringForGetNotesResponse(responseString);
                for (JsonForGetNotesResponse msg:message) {
                    textView.append(msg.toString());
                }
            }
        });

    }

}
