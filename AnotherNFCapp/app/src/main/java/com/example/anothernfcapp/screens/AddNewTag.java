package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.anothernfcapp.R;

public class AddNewTag extends Activity {
    EditText tagName;
    Button setUpTag;
    Button goBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_tag);
        tagName.findViewById(R.id.setTagName);
        setUpTag.findViewById(R.id.setTagNameButton);
        goBack.findViewById(R.id.goBackAddNewTag);

    }
}
