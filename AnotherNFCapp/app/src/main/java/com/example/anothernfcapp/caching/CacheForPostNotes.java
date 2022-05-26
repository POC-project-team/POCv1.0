package com.example.anothernfcapp.caching;

import android.content.Context;

import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.json.add_notes.JsonForAddNoteRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CacheForPostNotes {
    private Context context;
    private final String filename = "cacheFileForPostNotes";
    private final File cacheFilePostNotes;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final List<JsonForAddNoteRequest> notes;
    private Gson gson;
    private GsonBuilder gsonBuilder;


    public CacheForPostNotes(Context context) {
        this.context = context;
        cacheFilePostNotes = new File(context.getCacheDir(), filename);
        notes = new ArrayList<>();
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public void clearCache() throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(cacheFilePostNotes));
        bufferedWriter.write("");
        bufferedWriter.close();
    }

    public void writeToCache(String noteToCache) throws IOException {
        notes.addAll(sentinel());
        notes.add(gson.fromJson(noteToCache, JsonForAddNoteRequest.class));
        bufferedWriter = new BufferedWriter(new FileWriter(cacheFilePostNotes));
        gson.toJson(notes, bufferedWriter);
        bufferedWriter.close();
    }

    private List<JsonForAddNoteRequest> sentinel() throws IOException {
        if (cacheFilePostNotes.length() == 0){
            bufferedWriter = new BufferedWriter(new FileWriter(cacheFilePostNotes));
            bufferedWriter.write("[]");
            bufferedWriter.close();
        }
        FileReader fileReader = new FileReader(cacheFilePostNotes);
        return JsonFactory.makeJsonForAddNoteCache(fileReader);
    }

    public List<JsonForAddNoteRequest> getCache() throws IOException {
        return sentinel();
    }

    public File getCacheFilePostNotes() {
        return cacheFilePostNotes;
    }
}
