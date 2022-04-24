package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
            try {
                setUpTag();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        goBack.setOnClickListener(v -> goBack());
    }

    private void goBack() {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }

    private void setUpTag() throws UnsupportedEncodingException {
        String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "/tag";
        JsonFactory jsonFactory = new JsonFactory();
        String msg = jsonFactory.makeJsonForCreateTagRequest(tagName.getText().toString());
        StringEntity stringEntity = new StringEntity(msg);
        asyncHttpClient.post(this, url, stringEntity, msg, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                BadStatusCodeProcess badStatusCodeProcess = new BadStatusCodeProcess();
                badStatusCodeProcess.parseBadStatusCode(statusCode, responseString, AddNewTag.this);
                Log.e("ADDNEWTAG", "Status code: " + statusCode + " response: " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                createOnSuccessMessage();
                Log.d("ADDNEWTAG", "Successfully created new tag");
            }
        });


    }

    private void createOnSuccessMessage() {
        Toast.makeText(this, "Successfully created new tag", Toast.LENGTH_SHORT).show();
    }
}
