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
import com.example.anothernfcapp.json.register.JsonForRegisterUserRequest;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegisterUser extends Activity {
    EditText login;
    EditText password;
    Button register;
    Button goBack;
    String urlToRegister;
    AsyncHttpClient asyncHttpClient;
    JsonForRegisterUserRequest jsonForRegisterUserRequest;
    JsonFactory jsonFactory;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        jsonFactory = new JsonFactory();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);
        login = (EditText) findViewById(R.id.loginReg);
        password = (EditText) findViewById(R.id.passwordReg);
        register = (Button) findViewById(R.id.sendRegistration);
        goBack = (Button) findViewById(R.id.backToLogin);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGoBack();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendRegistration();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendRegistration() throws UnsupportedEncodingException {
        asyncHttpClient = new AsyncHttpClient();
        urlToRegister = StaticVariables.ipServerUrl + "signup";
        String msg = jsonFactory.makeJsonForRegisterUserRequest(login.getText().toString(), password.getText().toString());
        StringEntity stringEntity = new StringEntity(msg);
        asyncHttpClient.post(this, urlToRegister, stringEntity, msg, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("REGISTER", "Failed to connect to the server " + statusCode + " Response: " + responseString);
                BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, RegisterUser.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                onSuccessRegister();
            }
        });
    }

    private void setGoBack(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void onSuccessRegister(){
        Toast.makeText(this, "Successful registration", Toast.LENGTH_SHORT).show();
    }
}
