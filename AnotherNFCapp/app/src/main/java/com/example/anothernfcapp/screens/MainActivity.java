package com.example.anothernfcapp.screens;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.json.JsonFactory;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity {
    EditText login;
    EditText password;
    Button buttonLogin;
    Button buttonRegister;
    AsyncHttpClient asyncHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        buttonLogin = (Button) findViewById(R.id.loginButton);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loginApp();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonRegister = (Button) findViewById(R.id.registerButton);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void loginApp() throws UnsupportedEncodingException {
        asyncHttpClient = new AsyncHttpClient();
        String url = StaticVariables.ipServerUrl + "auth";
        JsonFactory jsonFactory = new JsonFactory();
        String msg = jsonFactory.makeJsonForAuthUser(login.getText().toString(), password.getText().toString());
        StringEntity stringEntity = new StringEntity(msg);
        asyncHttpClient.post(this, url, stringEntity, msg, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, MainActivity.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                parseJWT(responseString);
                login();
            }
        });

    }

    private void login() {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }

    private void register(){
        Intent registration = new Intent(this, RegisterUser.class);
        startActivity(registration);
    }

    private void parseJWT(String response){
        JsonFactory jsonFactory = new JsonFactory();
        StaticVariables.setJWT(jsonFactory.makeStringForResponseAuthUser(response).toString());
    }
}