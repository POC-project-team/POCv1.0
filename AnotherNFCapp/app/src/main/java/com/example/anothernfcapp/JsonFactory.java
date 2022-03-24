package com.example.anothernfcapp;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonFactory {
    public String makeJsonMessage(String value, String tagId){
        Message message = new Message(value, tagId);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Log.d("GSON", gson.toJson(message));
        return gson.toJson(message);
    }
}
