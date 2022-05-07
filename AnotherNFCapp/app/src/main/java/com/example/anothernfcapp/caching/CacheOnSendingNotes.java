package com.example.anothernfcapp.caching;

import android.content.Context;

import java.io.File;

public class CacheOnSendingNotes {
    private final String fileName = "cacheFileForSentNotes";
    private final File cacheFile;
    private final Context context;

    public CacheOnSendingNotes(Context context) {
        this.context = context;
        cacheFile = new File(context.getCacheDir(), fileName);
    }

    public void addFilesToCache(String notes){

    }
}
