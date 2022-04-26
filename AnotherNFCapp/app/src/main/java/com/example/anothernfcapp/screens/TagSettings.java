package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class TagSettings extends Activity {
    AsyncHttpClient asyncHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_settings);
        asyncHttpClient = new AsyncHttpClient();
        findViewById(R.id.startAddNewTag).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddNewTag.class);
            startActivity(intent);
        });
        findViewById(R.id.startChangeTagName).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangeTagName.class);
            startActivity(intent);
        });
        findViewById(R.id.deleteTag).setOnClickListener(v -> deleteTag());
        findViewById(R.id.goBackTagSettings).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        });
        findViewById(R.id.sendTag).setOnClickListener(v -> sendTag());
    }

    private void sendTag() {
        String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "/" + "send";
        Toast.makeText(this, "WIP", Toast.LENGTH_SHORT).show();
    }

    private void deleteTag() {
        if (StaticVariables.tagId == null){
            Toast.makeText(this, "Tag isn't set up", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "tag";
        asyncHttpClient.delete(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                BadStatusCodeProcess badStatusCodeProcess = new BadStatusCodeProcess();
                badStatusCodeProcess.parseBadStatusCode(statusCode, responseString, TagSettings.this);
                Log.e("DELETE", "Error: " + statusCode + " Response: " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                onSuccessfulDelete();
            }
        });
    }

    private void onSuccessfulDelete() {
        Toast.makeText(this, "Successfully deleted tag", Toast.LENGTH_SHORT).show();
        StaticVariables.tagId = null;
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }
}
