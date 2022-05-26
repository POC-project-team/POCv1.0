package com.example.anothernfcapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        testConnectionButton.setOnClickListener(v -> {
            if (StaticVariables.tagId == null){
                msgError();
            }
            else if(cacheForPostNotes.getCacheFilePostNotes().length() == 0){
                msgEmptyCache();
            } else {
                Log.d("POST", "onCreate: ");
                try {
                    testConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        getUserInfoButton = findViewById(R.id.userInfoButton);
        getUserInfoButton.setOnClickListener(v -> userInfo());
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());
        //changeLogin = findViewById(R.id.changeLogin);
        //        changeLogin.setOnClickListener(v -> changeLogin());
        //changePassword = findViewById(R.id.changePassword);
        //changePassword.setOnClickListener(v -> changePassword());
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

    private void testConnection() throws IOException {
        CacheForPostNotes cacheForPostNotes = new CacheForPostNotes(this);
        String urlToPost = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "/note";
        Log.d("POST", urlToPost);
        List<JsonForAddNoteRequest> json = cacheForPostNotes.getCache();
        Log.d("POST", "" + json.size());
        for (int i = 0; i < json.size(); i++){
            String message = JsonFactory.makeJsonForAddNoteRequest(json.get(i).note);
            Log.d("POST", message);
            StringEntity stringEntity = new StringEntity(message);
            int finalI = i;
            asyncHttpClient.post(this, urlToPost, stringEntity, message, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("POST", "Failed to connect to server. " + statusCode + " Response: " + responseString);
                    BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, Settings.this);
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("POST", "onSuccess");
                    makeToastMsg();
                    if (finalI == json.size() - 1){
                        try {
                            cacheForPostNotes.clearCache();
                            Log.d("POST", "CACHE WAS CLEARED");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

    }

    private void msgError(){
        Toast.makeText(this, "Tag isn't set up", Toast.LENGTH_SHORT).show();
    }

    private void msgEmptyCache() {
        Toast.makeText(this, "Cache is empty", Toast.LENGTH_SHORT).show();
    }


    private void makeToastMsg(){
        Toast.makeText(this, "Everything was sent", Toast.LENGTH_SHORT).show();
    }
}
