package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class ChangeLogin extends Activity {
    private Button changeLoginButton;
    private EditText login;
    private EditText newLogin;
    private EditText password;
    private CheckBox showPassword;
    private ImageButton goBack;
    private AsyncHttpClient asyncHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_login);
        asyncHttpClient = new AsyncHttpClient();
        login = findViewById(R.id.old_login);
        newLogin = findViewById(R.id.new_login);
        password = findViewById(R.id.passwordForChangeLogin);
        changeLoginButton = findViewById(R.id.buttonForChangeLogin);
        changeLoginButton.setOnClickListener(v -> {
            try {
                changeLogin();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        showPassword = findViewById(R.id.show_password_change_login);
        showPassword.setOnClickListener(v -> {
            if (showPassword.isChecked()){
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        goBack = findViewById(R.id.goBackButtonChangeLogin);
        goBack.setOnClickListener(v -> goBack());
    }

    private void changeLogin() throws UnsupportedEncodingException {
        if (login.getText().toString().equals(StaticVariables.login)){
            String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/changeLogin";
            String request = JsonFactory.makeJsonForChangeLoginRequest(newLogin.getText().toString(), password.getText().toString());
            StringEntity stringEntity = new StringEntity(request);
            asyncHttpClient.post(this, url, stringEntity, request, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, ChangeLogin.this);
                    Log.e("CHANGELOGIN", "Status code: " + statusCode + " response: " + responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("CHANGELOGIN", "Successfully change login");
                    printOnSuccess();
                    logout();
                }
            });
        }
        else if (login.getText().toString().equals("") || newLogin.getText().toString().equals("")){
            Toast.makeText(this, "Enter login", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "You can't change another login", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void printOnSuccess() {
        Toast.makeText(this, "Successfully changed your login", Toast.LENGTH_SHORT).show();
    }

    private void goBack() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }


}
