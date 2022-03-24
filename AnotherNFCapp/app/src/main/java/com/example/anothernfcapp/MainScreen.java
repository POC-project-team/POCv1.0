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
    public static String tagId;
    TextView tagIdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tagIdTextView = (TextView)findViewById(R.id.tagId);
        ((Button) findViewById(R.id.buttonwrite)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("POST", "Clicked on write button");
                if (tagId != null){
                    writeScreenStart();

                }
                else{
                    msgError();
                }
            }
        });
        ((Button) findViewById(R.id.buttonget)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GET", "Clicked on get button");
                if (tagId == null){
                    msgError();
                }
                else {
                    getDataViaTag();
                }
            }
        });
        ((Button) findViewById(R.id.buttonSetUp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void msgError(){
        Toast.makeText(this, "Tag isn't set up", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            tagId = bytesToHexString(detectedTag.getId());
            Log.d("TAG", tagId);
            tagIdTextView.setText("Current tagId: " + tagId);
            Toast.makeText(this, "Success: POCED THIS TAG", Toast.LENGTH_LONG).show();
        }
    }

    private void writeScreenStart(){
        Log.d("POST", "Write Screen");
        Intent intent = new Intent(this, WriteScreen.class);
        startActivity(intent);

    }

    private void getDataViaTag(){
        Log.d("GET", "getDataViaTag");
        Intent getScreen = new Intent(this, GetScreen.class);
        startActivity(getScreen);
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

    public String getTagId(){
        return tagId;
    }
}