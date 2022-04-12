package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.json.JsonForGetNotesResponse;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class GetScreen extends Activity {
    AsyncHttpClient asyncHttpClient;
    TextView textView;
    Button button;
    JsonFactory jsonFactory;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_screen);
        getJsonMessageFromServer();
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

    private void getJsonMessageFromServer() {
        asyncHttpClient = new AsyncHttpClient();
        String urlToGet = StaticVariables.ipServerUrl + "1/getNotes";
        Log.d("GET", urlToGet);
        StringEntity stringEntity = null;
        jsonFactory = new JsonFactory();
        String message = jsonFactory.makeJsonForGetNotesRequest(StaticVariables.tagId);
        try {
            stringEntity = new StringEntity(message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        asyncHttpClient.post(this, urlToGet, stringEntity, message, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("GET", "onFailure. Status code: " + statusCode);
                BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, GetScreen.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("GET", "onSuccess");
                JsonForGetNotesResponse[] message;
                message = jsonFactory.makeStringForGetNotesResponseFromRequest(responseString);
                for (JsonForGetNotesResponse msg:message) {
                    textView.append(msg.toString());
                }
            }
        });

    }

}
