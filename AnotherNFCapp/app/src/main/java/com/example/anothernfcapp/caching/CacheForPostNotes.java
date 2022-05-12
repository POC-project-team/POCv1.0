package com.example.anothernfcapp.caching;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

    public void writeToCache(String data) throws IOException {
        bufferedWriter = new BufferedWriter(new FileWriter(filename));
        bufferedWriter.append(data);
    }

}
