package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.anothernfcapp.R;

public class RegisterUser extends Activity {
    EditText login;
    EditText password;
    Button register;
    Button goBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
                sendRegistration();
            }
        });
    }

    private void sendRegistration() {
        Toast.makeText(this, "WIP", Toast.LENGTH_SHORT).show();

    }

    private void setGoBack(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
