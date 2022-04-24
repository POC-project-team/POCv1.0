package com.example.anothernfcapp.json;

import com.example.anothernfcapp.json.add_new_tag.JsonForCreateNewTagRequest;
import com.example.anothernfcapp.json.add_notes.JsonForAddNoteRequest;
import com.example.anothernfcapp.json.auth.JsonForAuthUserRequest;
import com.example.anothernfcapp.json.auth.JsonForAuthUserResponse;
import com.example.anothernfcapp.json.change_login.JsonForChangeLoginRequest;
import com.example.anothernfcapp.json.change_password.JsonForChangePasswordRequest;
import com.example.anothernfcapp.json.get_notes.JsonForGetNotesResponse;
import com.example.anothernfcapp.json.get_tags.JsonForGetTagsResponse;
import com.example.anothernfcapp.json.register.JsonForRegisterUserRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonFactory {
    public String makeJsonForAddNoteRequest(String value){
        JsonForAddNoteRequest jsonForAddNoteRequest = new JsonForAddNoteRequest(value);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForAddNoteRequest);
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

    public String makeJsonForChangeLoginRequest(String login, String password){
        JsonForChangeLoginRequest jsonForChangeLoginRequest = new JsonForChangeLoginRequest(login, password);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForChangeLoginRequest);
    }

    public String makeJsonForChangePasswordRequest(String login, String password){
        JsonForChangePasswordRequest jsonForChangePasswordRequest = new JsonForChangePasswordRequest(login, password);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForChangePasswordRequest);
    }

    public String makeJsonForCreateTagRequest(String tagName){
        JsonForCreateNewTagRequest json = new JsonForCreateNewTagRequest(tagName);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(json);
    }

    public JsonForGetTagsResponse[] makeStringForGetTagsResponse(String messageToParse){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.fromJson(messageToParse, JsonForGetTagsResponse[].class);

    }


}
