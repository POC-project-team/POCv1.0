package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private EditText login;
    private EditText password;
    private EditText confirmPassword;
    private CheckBox showPassword1;
    private CheckBox showPassword2;
    private Button register;
    private ImageButton goBack;
    private String urlToRegister;
    private AsyncHttpClient asyncHttpClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);
        login = findViewById(R.id.loginReg);
        password = findViewById(R.id.passwordReg);
        register = findViewById(R.id.sendRegistration);
        register.setOnClickListener(v -> {
            try {
                sendRegistration();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        confirmPassword = findViewById(R.id.passwordReg2);
        showPassword1 = findViewById(R.id.show_password_register1);
        showPassword1.setOnClickListener(v -> {
            if (showPassword1.isChecked()){
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        showPassword2 = findViewById(R.id.show_password_register2);
        showPassword2.setOnClickListener(v -> {
            if (showPassword2.isChecked()){
                confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        goBack = findViewById(R.id.backToLogin);
        goBack.setOnClickListener(v -> setGoBack());
    }

    private void sendRegistration() throws UnsupportedEncodingException {
        if (confirmPassword.getText().toString().equals(password.getText().toString())) {
            asyncHttpClient = new AsyncHttpClient();
            urlToRegister = StaticVariables.ipServerUrl + "signup";
            String msg = JsonFactory.makeJsonForRegisterUserRequest(login.getText().toString(), password.getText().toString());
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
        else if (confirmPassword.getText().toString().equals("")){
            Toast.makeText(this, "Please, confirm your password", Toast.LENGTH_SHORT).show();
        }
        else if (password.getText().toString().equals("")){
            Toast.makeText(this, "Please, write your password", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
        }
    }

    private void setGoBack(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void onSuccessRegister(){
        Toast.makeText(this, "Successful registration", Toast.LENGTH_SHORT).show();
    }
}
