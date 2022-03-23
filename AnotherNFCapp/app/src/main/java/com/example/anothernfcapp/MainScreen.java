package com.example.anothernfcapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainScreen extends AppCompatActivity {
    boolean mWriteMode = false;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    String value;
    private String serverUrl = "http://172.20.10.12:60494/";
    private int FLAG = -1;
    private String tagId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.buttonwrite)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG = 0;
                Log.d("POST", "Clicked on write button");
                nfcAdapter = NfcAdapter.getDefaultAdapter(MainScreen.this);
                nfcPendingIntent = PendingIntent.getActivity(MainScreen.this, 0,
                        new Intent(MainScreen.this, MainScreen.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                enableTagWriteMode();
                new AlertDialog.Builder(MainScreen.this).setTitle("Touch tag to write a data").setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        disableTagWriteMode();
                    }
                }).create().show();
            }
        });
        ((Button) findViewById(R.id.buttonget)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG = 1;
                Log.d("GET", "Clicked on get button");
                nfcAdapter = NfcAdapter.getDefaultAdapter(MainScreen.this);
                nfcPendingIntent = PendingIntent.getActivity(MainScreen.this, 0,
                        new Intent(MainScreen.this, MainScreen.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                enableTagWriteMode();
                new AlertDialog.Builder(MainScreen.this).setTitle("Touch tag to make a get request").setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        disableTagWriteMode();
                    }
                }).create().show();
            }
        });

    }

    private void disableTagWriteMode() {
        mWriteMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] mWriteTagFilters = new IntentFilter[] { tagDetected };
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, mWriteTagFilters, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            tagId = bytesToHexString(detectedTag.getId());
            Log.d("TAG", tagId);
            value = ((TextView)findViewById(R.id.valueFromServer)).getText().toString();
            if (FLAG == 0) {
                try {
                    postMessage(value);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Success: POCED THIS TAG", Toast.LENGTH_LONG).show();
            }
            if (FLAG == 1){
                getDataViaTag();
                Toast.makeText(this, "Success: POCED THIS TAG", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getDataViaTag(){
        Log.d("GET", "getDataViaTag");
        startGetScreen();
    }

    public void startGetScreen(){
        Intent getScreen = new Intent(this, GetScreen.class);
        startActivity(getScreen);
    }

    private void postMessage(String text) throws UnsupportedEncodingException {
        JsonFactory jsonFactory = new JsonFactory();
        String msg = jsonFactory.makeJsonMessage(text);
        String urlToPost = serverUrl + "1/0/addNote";
        Log.d("POST", urlToPost);
        Log.d("POST", text);
        StringEntity stringEntity = new StringEntity(msg);
        asyncHttpClient.post(this, urlToPost, stringEntity, msg, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("POST", "Can't connect to the server. Connecting to google");
                Log.e("POST", "Status code: " + statusCode);
                asyncHttpClient.post("https://www.google.com", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("POST", "Connected to Google");
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("POST", "Status code: " + statusCode);
                    }
                });
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("POST", "onSuccess");
            }
        });
    }
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }
}