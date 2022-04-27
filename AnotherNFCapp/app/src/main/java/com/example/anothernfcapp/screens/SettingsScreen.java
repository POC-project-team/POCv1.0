package com.example.anothernfcapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class SettingsScreen extends AppCompatActivity {
    Button testConnectionButton;
    Button getUserInfoButton;
    Button logoutButton;
    Button clearTagId;
    Button goBack;
    Button dungeonMasterButton;
    AsyncHttpClient asyncHttpClient;
    BadStatusCodeProcess badStatusCodeProcess;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        asyncHttpClient = new AsyncHttpClient();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        goBack = findViewById(R.id.goBackSettingsScreen);
        dungeonMasterButton = findViewById(R.id.iAmBossOfTheGym);
        clearTagId = findViewById(R.id.clearTagButton);
        badStatusCodeProcess = new BadStatusCodeProcess();
        testConnectionButton = findViewById(R.id.testConnectionButton);
        getUserInfoButton = findViewById(R.id.userInfoButton);
        logoutButton = findViewById(R.id.logoutButton);
        testConnectionButton.setOnClickListener(v -> testConnection());

        getUserInfoButton.setOnClickListener(v -> userInfo());
        logoutButton.setOnClickListener(v -> logout());
        clearTagId.setOnClickListener(v -> clearTagId());
        dungeonMasterButton.setOnClickListener(v -> dungeonMaster());
        goBack.setOnClickListener(v -> goBack());
    }

    private void goBack() {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }

    private void dungeonMaster() {
        if (StaticVariables.login.equals(StaticVariables.superUser)){
            StaticVariables.setTagId(StaticVariables.superTagId);
            Toast.makeText(this, "Successfully set up tag", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearTagId() {
        StaticVariables.tagId = null;
        Toast.makeText(this, "Successfully cleared tagID", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void userInfo() {
        Intent intent = new Intent(this, UserInformation.class);
        startActivity(intent);
    }

    private void testConnection(){
        String url = StaticVariables.ipServerUrl + "test";
        Log.d("TEST", url);
        asyncHttpClient.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                badStatusCodeProcess.parseBadStatusCode(statusCode, responseString, SettingsScreen.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                makeToastMsg(statusCode);
                Log.d("TEST", "onSuccess");
            }
        });
    }

    private void makeToastMsg(int statusCode){
        if (statusCode >= 200 && statusCode < 400){
            Toast.makeText(this, "Server is working", Toast.LENGTH_SHORT).show();
        }
    }
}
