package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class TagSettings extends Activity {
    AsyncHttpClient asyncHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_settings);
        asyncHttpClient = new AsyncHttpClient();
        findViewById(R.id.startAddNewTag).setOnClickListener(v -> addNewTag());
        findViewById(R.id.startChangeTagName).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangeTagName.class);
            startActivity(intent);
        });
        findViewById(R.id.deleteTag).setOnClickListener(v -> deleteTag());
        findViewById(R.id.goBackTagSettings).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        });
        findViewById(R.id.sendTag).setOnClickListener(v -> sendTag());
    }

    private void addNewTag() {
        String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "/tag";
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View addNewTagView = layoutInflater.inflate(R.layout.add_new_tag, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(addNewTagView);
        EditText tagName = addNewTagView.findViewWithTag(R.id.setTagName);
        builder.setPositiveButton("Set", (dialog, which) -> {
            JsonFactory jsonFactory = new JsonFactory();
            String msg = jsonFactory.makeJsonForCreateTagRequest(tagName.getText().toString());
            StringEntity stringEntity = null;
            try {
                stringEntity = new StringEntity(msg);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            asyncHttpClient.post(this, url, stringEntity, msg, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    BadStatusCodeProcess badStatusCodeProcess = new BadStatusCodeProcess();
                    badStatusCodeProcess.parseBadStatusCode(statusCode, responseString, TagSettings.this);
                    Log.e("ADDNEWTAG", "Status code: " + statusCode + " response: " + responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    createOnSuccessMessage();
                    Log.d("ADDNEWTAG", "Successfully created new tag");
                }
            });
        }).setNegativeButton("Cancel", ((dialog, which) -> {
            dialog.cancel();
        }));
    }

    private void createOnSuccessMessage() {
        Toast.makeText(this, "Successfully created new tag", Toast.LENGTH_SHORT).show();
    }

    private void sendTag() {
        String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "/" + "send";
        LayoutInflater li = LayoutInflater.from(this);
        View sendTagView = li.inflate(R.layout.send_tag, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(sendTagView);
        EditText login = sendTagView.findViewById(R.id.loginToSend);
        builder.setPositiveButton("Send", (dialog, which) -> {
            JsonFactory jsonFactory = new JsonFactory();
            String msg = jsonFactory.makeJsonForSendTagRequest(login.getText().toString());
            StringEntity stringEntity = null;
            try {
                stringEntity = new StringEntity(msg);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            asyncHttpClient.post(this, url, stringEntity, msg, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    BadStatusCodeProcess badStatusCodeProcess = new BadStatusCodeProcess();
                    badStatusCodeProcess.parseBadStatusCode(statusCode, responseString, TagSettings.this);
                    Log.e("SENDTAG", "Error: " + statusCode + " response: " + responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    showOnSuccess();
                }
            });
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

    }

    private void showOnSuccess() {
        Toast.makeText(this, "Successfully sent this tag", Toast.LENGTH_SHORT).show();
    }

    private void deleteTag() {
        if (StaticVariables.tagId == null){
            Toast.makeText(this, "Tag isn't set up", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/" + StaticVariables.tagId + "tag";
        asyncHttpClient.delete(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                BadStatusCodeProcess badStatusCodeProcess = new BadStatusCodeProcess();
                badStatusCodeProcess.parseBadStatusCode(statusCode, responseString, TagSettings.this);
                Log.e("DELETE", "Error: " + statusCode + " Response: " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                onSuccessfulDelete();
            }
        });
    }

    private void onSuccessfulDelete() {
        Toast.makeText(this, "Successfully deleted tag", Toast.LENGTH_SHORT).show();
        StaticVariables.tagId = null;
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }
}
