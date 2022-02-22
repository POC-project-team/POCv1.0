package com.example.proto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    private static final String ERROR_DETECTED = "No NFC tag";
    private static final String SUCCESS = "Successfully added";
    private static final String ERROR_WRITING = "Error during writing";
    private static final String CLICKED = "CLICKED";
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilter;
    private boolean writeMode;
    public Tag tag;
    private Context context;
    private TextView textView;
    private TextView nfc;
    private Button button;

    // не отрабатывает метод
    @Override
    protected void onNewIntent(Intent intent) {
        Toast.makeText(this, "ON NEW INTENT", Toast.LENGTH_SHORT).show();
        super.onNewIntent(intent);
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        setIntent(intent);
        readFromIntent(intent);
        Toast.makeText(this, "TAG DISCOVERED", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        nfc = (TextView) findViewById(R.id.content);
        button = findViewById(R.id.button);
        context = this;
        writingOnCreate(getIntent());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Toast.makeText(context, CLICKED, Toast.LENGTH_SHORT).show();
                    //BUG: tag is always equals null
                    if (tag == null) {
                        Toast.makeText(context, "Tag == null", Toast.LENGTH_SHORT).show();
                    } else {
                        write(textView.getText().toString(), tag);
                        Toast.makeText(context, "Wrote successfully", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC chip", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is enabled", Toast.LENGTH_SHORT).show();
        }

        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this,
                0,
                new Intent(this, getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter = new IntentFilter[]{tagDetected};
    }

    private void write(String toString, Tag tag) {
        NdefRecord[] records = {createRecord(toString)};
        NdefMessage ndefMessage = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        try {
            ndef.writeNdefMessage(ndefMessage);
            ndef.close();
        } catch (FormatException | IOException formatException) {
            formatException.printStackTrace();
        }
    }

    private NdefRecord createRecord(String toString) {
        String language = "en";
        byte[] textBytes = toString.getBytes();
        byte[] languageBytes = null;
        try {
            languageBytes = language.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int languageLength = languageBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + languageLength + textLength];
        payload[0] = (byte) languageLength;
        System.arraycopy(languageBytes, 0, payload, 1, languageLength);
        System.arraycopy(textBytes, 0, payload, 1 + languageLength, textLength);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
    }


    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] raw = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] messages = null;
            if (raw != null) {
                messages = new NdefMessage[raw.length];
                for (int i = 0; i < raw.length; i++) {
                    messages[i] = (NdefMessage) raw[i];

                }
            }
            buildTagViews(messages);

        }
    }

    private void buildTagViews(NdefMessage[] messages) {
        if (messages == null || messages.length == 0) {
            return;
        }
        String text = "";
        byte[] payload = messages[0].getRecords()[0].getPayload();
        String textEncode = ((payload)[0] & 128) == 0 ? "UTF-8" : "UTF-16";
        int codeLen = payload[0] & 0063;
        try {
            text = new String(payload, codeLen + 1, payload.length - codeLen, textEncode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        nfc.setText("NFC Content: " + text);
    }


    @Override
    public void onPause() {
        super.onPause();
        writeModeOff();
    }

    private void writeModeOff() {
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        writeModeOn();
    }

    private void writeModeOn() {
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    /*
     * new method
     * */
    private void writingOnCreate(Intent intent) {
        Toast.makeText(this, "STARTING ANAL DISASTER", Toast.LENGTH_SHORT).show();
        String action = intent.getAction();
        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) || action.equals(NfcAdapter.ACTION_TECH_DISCOVERED) || action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
        setIntent(intent);
        readFromIntent(intent);
    }
}