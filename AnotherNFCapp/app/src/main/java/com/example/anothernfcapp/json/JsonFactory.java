package com.example.anothernfcapp.json;

import com.example.anothernfcapp.json.add_notes.JsonForAddNoteRequest;
import com.example.anothernfcapp.json.auth.JsonForAuthUserRequest;
import com.example.anothernfcapp.json.auth.JsonForAuthUserResponse;
import com.example.anothernfcapp.json.get_notes.JsonForGetNotesRequest;
import com.example.anothernfcapp.json.get_notes.JsonForGetNotesResponse;
import com.example.anothernfcapp.json.register.JsonForRegisterUserRequest;
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

    public JsonForGetNotesResponse[] makeStringForGetNotesResponse(String messageToParse){
        JsonForGetNotesResponse[] parsedMessage;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        parsedMessage = gson.fromJson(messageToParse, JsonForGetNotesResponse[].class);
        return parsedMessage;
    }

    public String makeJsonForAuthUserRequest(String login, String password){
        JsonForAuthUserRequest jsonForAuthUser = new JsonForAuthUserRequest(login, password);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForAuthUser);

    }

    public JsonForAuthUserResponse makeStringForAuthUserResponse(String messageToParse){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.fromJson(messageToParse, JsonForAuthUserResponse.class);
    }

    public String makeJsonForRegisterUserRequest(String login, String password){
        JsonForRegisterUserRequest jsonForRegisterUserRequest = new JsonForRegisterUserRequest(login, password);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForRegisterUserRequest);
    }


}
