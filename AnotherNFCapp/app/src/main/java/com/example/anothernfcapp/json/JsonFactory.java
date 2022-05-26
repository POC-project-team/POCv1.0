package com.example.anothernfcapp.json;

import com.example.anothernfcapp.json.add_new_tag.JsonForCreateNewTagRequest;
import com.example.anothernfcapp.json.add_notes.JsonForAddNoteRequest;
import com.example.anothernfcapp.json.auth.JsonForAuthUserRequest;
import com.example.anothernfcapp.json.auth.JsonForAuthUserResponse;
import com.example.anothernfcapp.json.change_login.JsonForChangeLoginRequest;
import com.example.anothernfcapp.json.change_password.JsonForChangePasswordRequest;
import com.example.anothernfcapp.json.change_tag_name.JsonForChangeTagNameRequest;
import com.example.anothernfcapp.json.get_notes.JsonForGetNotesResponse;
import com.example.anothernfcapp.json.get_tags.JsonForGetTagsResponse;
import com.example.anothernfcapp.json.register.JsonForRegisterUserRequest;
import com.example.anothernfcapp.json.send.JsonForSendTagRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

public class JsonFactory {
    public static String makeJsonForAddNoteRequest(String value){
        JsonForAddNoteRequest jsonForAddNoteRequest = new JsonForAddNoteRequest(value);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForAddNoteRequest);
    }

    public static JsonForGetNotesResponse[] makeStringForGetNotesResponse(String messageToParse){
        JsonForGetNotesResponse[] parsedMessage;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        parsedMessage = gson.fromJson(messageToParse, JsonForGetNotesResponse[].class);
        return parsedMessage;
    }

    public static String makeJsonForAuthUserRequest(String login, String password){
        JsonForAuthUserRequest jsonForAuthUser = new JsonForAuthUserRequest(login, password);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForAuthUser);

    }

    public static JsonForAuthUserResponse makeStringForAuthUserResponse(String messageToParse){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.fromJson(messageToParse, JsonForAuthUserResponse.class);
    }

    public static String makeJsonForRegisterUserRequest(String login, String password){
        JsonForRegisterUserRequest jsonForRegisterUserRequest = new JsonForRegisterUserRequest(login, password);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForRegisterUserRequest);
    }

    public static String makeJsonForChangeLoginRequest(String login, String password){
        JsonForChangeLoginRequest jsonForChangeLoginRequest = new JsonForChangeLoginRequest(login, password);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForChangeLoginRequest);
    }

    public static String makeJsonForChangePasswordRequest(String login, String password){
        JsonForChangePasswordRequest jsonForChangePasswordRequest = new JsonForChangePasswordRequest(login, password);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(jsonForChangePasswordRequest);
    }

    public static String makeJsonForCreateTagRequest(String tagName){
        JsonForCreateNewTagRequest json = new JsonForCreateNewTagRequest(tagName);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(json);
    }

    public static JsonForGetTagsResponse[] makeStringForGetTagsResponse(String messageToParse){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.fromJson(messageToParse, JsonForGetTagsResponse[].class);

    }

    public static String makeJsonForChangeTagNameRequest(String tagName){
        JsonForChangeTagNameRequest json = new JsonForChangeTagNameRequest(tagName);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(json);
    }

    public static String makeJsonForSendTagRequest(String login){
        JsonForSendTagRequest json = new JsonForSendTagRequest(login);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(json);
    }

    public static List<JsonForAddNoteRequest> makeJsonForAddNoteCache(FileReader fileReader){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return Arrays.asList(gson.fromJson(fileReader, JsonForAddNoteRequest[].class));
    }


}
