package com.example.anothernfcapp.caching;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

public class CacheForPostNotes {
    private Context context;
    private final String filename = "cacheFileForPostNotes";
    private final File cacheFilePostNotes;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public CacheForPostNotes(Context context){
        this.context = context;
        cacheFilePostNotes = new File(context.getCacheDir(), filename);
    }

    public void writeToCache(String data){

    }

}
