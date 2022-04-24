package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;

public class AddNewTag extends Activity {
    EditText tagName;
    Button setUpTag;
    Button goBack;
    AsyncHttpClient asyncHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_tag);
        asyncHttpClient = new AsyncHttpClient();
        tagName.findViewById(R.id.setTagName);
        setUpTag.findViewById(R.id.setTagNameButton);
        goBack.findViewById(R.id.goBackAddNewTag);
        setUpTag.setOnClickListener(v -> {
            setUpTag();
        });
        goBack.setOnClickListener(v -> {
            goBack();
        });
    }

    private void goBack() {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }

    private void setUpTag() {
        String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "/tag";


    }
}
