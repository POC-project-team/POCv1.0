package com.example.anothernfcapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class Settings extends AppCompatActivity {
    private Button testConnectionButton;
    private Button getUserInfoButton;
    private Button logoutButton;
    private Button clearTagId;
    private Button goBack;
    private Button dungeonMasterButton;
    private Button changeLogin;
    private Button changePassword;
    private AsyncHttpClient asyncHttpClient;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        asyncHttpClient = new AsyncHttpClient();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        goBack = findViewById(R.id.goBackSettingsScreen);
        goBack.setOnClickListener(v -> goBack());
        dungeonMasterButton = findViewById(R.id.iAmBossOfTheGym);
        dungeonMasterButton.setOnClickListener(v -> dungeonMaster());
        clearTagId = findViewById(R.id.clearTagButton);
        clearTagId.setOnClickListener(v -> clearTagId());
        testConnectionButton = findViewById(R.id.testConnectionButton);
        testConnectionButton.setOnClickListener(v -> testConnection());
        getUserInfoButton = findViewById(R.id.userInfoButton);
        getUserInfoButton.setOnClickListener(v -> userInfo());
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());
        changeLogin = findViewById(R.id.changeLogin);
        changeLogin.setOnClickListener(v -> changeLogin());
        changePassword = findViewById(R.id.changePassword);
        changePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }

    private void changeLogin() {
        Intent intent = new Intent(this, ChangeLogin.class);
        startActivity(intent);
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
                BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, Settings.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                makeToastMsg();
                Log.d("TEST", "onSuccess");
            }
        });
    }

    private void makeToastMsg(){
        Toast.makeText(this, "Server is working", Toast.LENGTH_SHORT).show();
    }
}
