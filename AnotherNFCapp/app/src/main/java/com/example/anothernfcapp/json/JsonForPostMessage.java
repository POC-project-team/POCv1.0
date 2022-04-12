package com.example.anothernfcapp.json;

public class JsonForPostMessage {

    public String note;
    public String tagID;
    JsonForPostMessage(String value, String tagId){
        this.tagID = tagId;
        note = value;
    }

    
}