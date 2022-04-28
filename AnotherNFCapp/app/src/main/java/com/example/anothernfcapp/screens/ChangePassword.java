package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class ChangePassword extends Activity {
    EditText login;
    EditText oldPassword;
    EditText newPassword;
    Button changePasswordButton;
    Button goBackButton;
    AsyncHttpClient asyncHttpClient;
    JsonFactory jsonFactory;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        login = findViewById(R.id.loginForChangePassword);
        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);
        changePasswordButton = findViewById(R.id.buttonForChangePassword);
        changePasswordButton.setOnClickListener(v -> {
            try {
                changePassword();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        goBackButton = findViewById(R.id.goBackButtonChangePassword);
        goBackButton.setOnClickListener(v -> goBack());
    }

    private void changePassword() throws UnsupportedEncodingException {
        if (login.getText().toString().equals(StaticVariables.login) &&
                oldPassword.getText().toString().equals(newPassword.getText().toString())){
            String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/loginPassword";
            String request = jsonFactory.makeJsonForChangePasswordRequest(login.getText().toString(), newPassword.getText().toString());
            StringEntity stringEntity = new StringEntity(request);
            asyncHttpClient.post(this, url, stringEntity, request, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    BadStatusCodeProcess badStatusCodeProcess = new BadStatusCodeProcess();
                    badStatusCodeProcess.parseBadStatusCode(statusCode, responseString, ChangePassword.this);
                    Log.e("CHANGEPASSWORD", "Status code: " + statusCode + " response: " + responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    printSuccess();
                    try {
                        wait(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logout();
                }
            });
        }

    }

    private void logout() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void printSuccess() {
        Toast.makeText(this, "Successfully changed your password", Toast.LENGTH_SHORT).show();
    }


    private void goBack() {
        Intent intent = new Intent(this, SettingsScreen.class);
        startActivity(intent);
    }

}
