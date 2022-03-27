package com.example.anothernfcapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText login;
    EditText password;
    private final String LOGIN = "admin";
    private final String PASSWORD = "admin";
    Button buttonLogin;
    Button buttonRegistarte;
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
                loginApp();
            }
        });
        buttonRegistarte = (Button) findViewById(R.id.registerButton);
        buttonRegistarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void loginApp() {
        if (String.valueOf(login.getText()).equals(LOGIN) && String.valueOf(password.getText()).equals(PASSWORD)){
            Intent mainActivity = new Intent(this, MainScreen.class);
            startActivity(mainActivity);
        }
        else {
            Toast.makeText(this, "Wrong login or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void register(){
        Intent registration = new Intent(this, RegisterUser.class);
        startActivity(registration);
    }

}