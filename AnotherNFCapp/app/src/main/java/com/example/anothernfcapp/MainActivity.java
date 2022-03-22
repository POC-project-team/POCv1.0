package com.example.anothernfcapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import java.io.IOException;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    boolean mWriteMode = false;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    String value;
    private String serverUrl = "https://172.20.10.12:60494/";
    private int FLAG = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.buttonwrite)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
                nfcPendingIntent = PendingIntent.getActivity(MainActivity.this, 0,
                        new Intent(MainActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                enableTagWriteMode();
                new AlertDialog.Builder(MainActivity.this).setTitle("Touch tag to POC").setOnCancelListener(new DialogInterface.OnCancelListener() {
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
        // Tag writing mode
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            value = ((TextView)findViewById(R.id.value)).getText().toString();
            NdefRecord record = NdefRecord.createMime( "text/plain", ((TextView)findViewById(R.id.value)).getText().toString().getBytes());
            NdefMessage message = new NdefMessage(new NdefRecord[] { record });
            if (writeTag(message, detectedTag, value)) {
                Toast.makeText(this, "Success: POCED THIS TAG", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean writeTag(NdefMessage message, Tag detectedTag, String value) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(detectedTag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(getApplicationContext(),
                            "Can not write",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(getApplicationContext(),
                            "No enough space",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                ndef.writeNdefMessage(message);
                getJsonMessageFromServer();
                postMessage(value);
                //sendJsonMsg(value);
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(detectedTag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        getJsonMessageFromServer();
                        postMessage(value);
                        //sendJsonMsg(value);
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                } else {
                    return false;
                }

            }
        } catch (Exception e) {
            return false;
        }
    }

    private void getJsonMessageFromServer() {
        // Пока пускай будет оно, мало ли
        //String urlTg = "https://api.telegram.org/bot1755515654:AAH_rliJKbSPdD333cztAtLf1fLC0DG7IfI/sendMessage?chat_id=651346476&text=" + text;
        //Toast.makeText(this, urlTg, Toast.LENGTH_SHORT).show();
        //Log.d("LINK", urlTg);

        String urlToGet = serverUrl + "getUsers";
        Log.d("LINK", urlToGet);
        asyncHttpClient.get(urlToGet, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("LINK", "onSuccess");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("LINK", "Can't connect to the server. Connecting to google");
                asyncHttpClient.get("https://www.google.com", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("LINK", "Connected to Google");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("LINK", "No internet connection");
                    }
                });
            }
        });
    }

    private void postMessage(String text){
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.makeJsonMessage(text);
    }
/*
    private void sendJsonMsg(String text){
        String url = "https://api.telegram.org/bot1755515654:AAH_rliJKbSPdD333cztAtLf1fLC0DG7IfI/sendMessage?chat_id=651346476&text=" + text; //insert url here
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonFactory jsonFactory = new JsonFactory();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, );


    }*/
}