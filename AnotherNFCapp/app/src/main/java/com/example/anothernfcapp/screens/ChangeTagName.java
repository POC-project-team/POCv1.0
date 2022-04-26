package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ChangeTagName extends Activity {
    EditText tagName;
    AsyncHttpClient asyncHttpClient;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_tag_name);
        asyncHttpClient = new AsyncHttpClient();
        tagName = findViewById(R.id.newTagName);
        findViewById(R.id.changeTagName).setOnClickListener(v -> {
            try {
                changeTagName();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.goBackChangeTagName).setOnClickListener(v -> {
            Intent intent = new Intent(this, TagSettings.class);
            startActivity(intent);
        });
    }

    private void changeTagName() throws UnsupportedEncodingException {
        String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "/tag";
        JsonFactory jsonFactory = new JsonFactory();
        String request = jsonFactory.makeJsonForChangeTagNameRequest(tagName.getText().toString());
        StringEntity stringEntity = new StringEntity(request);
        asyncHttpClient.put(this, url, stringEntity, request, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                BadStatusCodeProcess badStatusCodeProcess = new BadStatusCodeProcess();
                badStatusCodeProcess.parseBadStatusCode(statusCode, responseString, ChangeTagName.this);
                Log.e("UPDATETAGNAME", "Error: " + statusCode + " response: " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                onSuccessfulChange();
            }
        });

    }

    private void onSuccessfulChange() {
        Toast.makeText(this, "Successfully changed name", Toast.LENGTH_SHORT).show();
    }
}
