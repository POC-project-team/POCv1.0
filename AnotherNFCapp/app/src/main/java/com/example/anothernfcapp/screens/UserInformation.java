package com.example.anothernfcapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anothernfcapp.R;

public class UserInformation extends AppCompatActivity {
    TextView nickname;
    TextView tags;
    Button backButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_layout);
        backButton = findViewById(R.id.goBackButtonUserInf);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton();
            }
        });
    }

    private void backButton() {
        Intent intent = new Intent(this, SettingsScreen.class);
        startActivity(intent);
    }
}
