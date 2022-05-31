package com.example.anothernfcapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.caching.CacheForPostNotes;
import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.json.add_notes.JsonForAddNoteRequest;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Settings extends AppCompatActivity {
    private Button testConnectionButton;
    private Button getUserInfoButton;
    private Button logoutButton;
    private Button clearTagId;
    private ImageButton goBack;
    private Button dungeonMasterButton;
    private Button changeLogin;
    private Button changePassword;
    private AsyncHttpClient asyncHttpClient;
    private CacheForPostNotes cacheForPostNotes;
    private int status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        cacheForPostNotes = new CacheForPostNotes(this);
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
        if (StaticVariables.login.equals(StaticVariables.superUser)) {
            dungeonMasterButton.setVisibility(View.VISIBLE);
        }
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

    private void testConnection() {
        String urlToPost = StaticVariables.ipServerUrl + "test";
        asyncHttpClient.get(urlToPost, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showConnection(responseString, statusCode);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                showConnection(responseString, statusCode);
            }
        });

    }

    private void showConnection(String responseString, int statusCode) {
        if (statusCode == 0){
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, responseString, Toast.LENGTH_SHORT).show();
        }
    }
}
