package com.example.anothernfcapp.json;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonFactory {
    public String makeJsonMessage(String value, String tagId){
        JsonForPostMessage jsonForPostMessage = new JsonForPostMessage(value, tagId);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Log.d("GSON", gson.toJson(jsonForPostMessage));
        return gson.toJson(jsonForPostMessage);
    }
    public String makeJsonMessageForGetNote(String tagID){
        JsonForGetNotes jsonForGetNotes = new JsonForGetNotes(tagID);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForGetNotes);

    }
}
