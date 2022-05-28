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

public class ChangePassword extends Activity {
    private EditText login;
    private EditText newPassword;
    private CheckBox changePassword1;
    private CheckBox changePassword2;
    private EditText confirmPassword;
    private Button changePasswordButton;
    private ImageButton goBackButton;
    private AsyncHttpClient asyncHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        asyncHttpClient = new AsyncHttpClient();
        login = findViewById(R.id.loginForChangePassword);
        newPassword = findViewById(R.id.old_password);
        confirmPassword = findViewById(R.id.new_password);
        changePasswordButton = findViewById(R.id.buttonForChangePassword);
        changePasswordButton.setOnClickListener(v -> {
            try {
                changePassword();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        changePassword1 = findViewById(R.id.show_password_change_password1);
        changePassword1.setOnClickListener(v -> {
            if (changePassword1.isChecked()){
                newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        changePassword2 = findViewById(R.id.show_password_change_password2);
        changePassword2.setOnClickListener(v -> {
            if (changePassword2.isChecked()){
                confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        goBackButton = findViewById(R.id.goBackButtonChangePassword);
        goBackButton.setOnClickListener(v -> goBack());
    }

    private void changePassword() throws UnsupportedEncodingException {
        if (login.getText().toString().equals(StaticVariables.login) && newPassword.getText().toString().equals(confirmPassword.getText().toString())){
            String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/changePassword";
            String request = JsonFactory.makeJsonForChangePasswordRequest(login.getText().toString(), confirmPassword.getText().toString());
            StringEntity stringEntity = new StringEntity(request);
            asyncHttpClient.post(this, url, stringEntity, request, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, ChangePassword.this);
                    Log.e("CHANGEPASSWORD", "Status code: " + statusCode + " response: " + responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    printSuccess();
                    logout();
                }
            });
        }
        else if (newPassword.getText().toString().equals("") || confirmPassword.getText().toString().equals("")){
            Toast.makeText(this, "Enter new password", Toast.LENGTH_SHORT).show();
        }
        else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())){
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "You can't change password of another user", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

}
