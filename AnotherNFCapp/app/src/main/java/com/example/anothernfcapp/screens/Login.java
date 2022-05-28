package com.example.anothernfcapp.screens;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Login extends AppCompatActivity {
    private EditText login;
    private EditText password;
    private Button buttonLogin;
    private Button buttonRegister;
    private CheckBox showPassword;
    private AsyncHttpClient asyncHttpClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.loginButton);
        buttonLogin.setOnClickListener(v -> {
            try {
                loginApp();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        buttonRegister = findViewById(R.id.registerButton);
        buttonRegister.setOnClickListener(v -> register());
        showPassword = findViewById(R.id.show_password_login);
        showPassword.setOnClickListener(v -> {
            if (showPassword.isChecked()){
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    private void loginApp() throws UnsupportedEncodingException {
        asyncHttpClient = new AsyncHttpClient();
        String url = StaticVariables.ipServerUrl + "auth";
        String msg = JsonFactory.makeJsonForAuthUserRequest(login.getText().toString(), password.getText().toString());
        StringEntity stringEntity = new StringEntity(msg);
        asyncHttpClient.post(this, url, stringEntity, msg, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("LOGIN", "Failed to connect to the server " + statusCode + " Response: " + responseString);
                BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, Login.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("LOGIN", "Successfully connected to the server");
                Log.d("LOGIN", responseString);
                parseJWT(responseString);
                StaticVariables.setLogin(login.getText().toString());
                StaticVariables.setPassword(password.getText().toString());
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
        StaticVariables.setJWT(JsonFactory.makeStringForAuthUserResponse(response).toString());
        Log.d("LOGIN", StaticVariables.JWT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}