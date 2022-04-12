package com.example.anothernfcapp.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonFactory {
    public String makeJsonForAddNoteRequest(String value, String tagId){
        JsonForAddNoteRequest jsonForAddNoteRequest = new JsonForAddNoteRequest(value, tagId);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForAddNoteRequest);
    }

    public String makeJsonForGetNotesRequest(String tagID){
        JsonForGetNotesRequest jsonForGetNotesRequest = new JsonForGetNotesRequest(tagID);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForGetNotesRequest);
    }

    public JsonForGetNotesResponse[] makeStringForGetNotesResponseFromRequest(String messageToParse){
        JsonForGetNotesResponse[] parsedMessage;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        parsedMessage = gson.fromJson(messageToParse, JsonForGetNotesResponse[].class);
        return parsedMessage;
    }

    public String makeJsonForAuthUser(String login, String password){
        JsonForPOSTAuthUser jsonForAuthUser = new JsonForPOSTAuthUser(login, password);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForAuthUser);

    }

    public JsonForResponseAuthUser makeStringForResponseAuthUser(String messageToParse){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.fromJson(messageToParse, JsonForResponseAuthUser.class);
    }


}
