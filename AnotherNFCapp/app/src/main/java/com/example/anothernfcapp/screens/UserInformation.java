package com.example.anothernfcapp.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.json.get_tags.JsonForGetTagsResponse;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class UserInformation extends AppCompatActivity {
    private TextView nickname;
    private TextView tags;
    private ImageButton backButton;
    private AsyncHttpClient asyncHttpClient;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_layout);
        asyncHttpClient = new AsyncHttpClient();
        nickname = findViewById(R.id.user_name);
        tags = findViewById(R.id.available_tags);
        backButton = findViewById(R.id.goBackButtonUserInf);
        backButton.setOnClickListener(v -> backButton());
        setName();
        setTags();
    }

    @SuppressLint("SetTextI18n")
    private void setTags() {
        String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/tags";
        Log.d("GETTAGS", url);
        asyncHttpClient.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                BadStatusCodeProcess.parseBadStatusCode(statusCode, responseString, UserInformation.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JsonForGetTagsResponse[] json = JsonFactory.makeStringForGetTagsResponse(responseString);
                if (json == null){
                    tags.append("No data found");
                    return;
                }
                for (JsonForGetTagsResponse item : json) {
                    tags.append(item.toString());
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setName() {
        nickname.setText(StaticVariables.login);
    }

    private void backButton() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
