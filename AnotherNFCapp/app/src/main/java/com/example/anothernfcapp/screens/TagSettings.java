package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.anothernfcapp.R;

public class TagSettings extends Activity {
    Button addNewTag;
    Button changeTagName;
    Button deleteTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_settings);
        findViewById(R.id.startAddNewTag).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddNewTag.class);
            startActivity(intent);
        });
        findViewById(R.id.startChangeTagName).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangeTagName.class);
            startActivity(intent);
        });

    }
}
