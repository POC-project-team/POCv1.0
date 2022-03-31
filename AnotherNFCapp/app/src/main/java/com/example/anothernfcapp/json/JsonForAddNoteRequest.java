package com.example.anothernfcapp.json;

public class JsonForAddNoteRequest {
    public String note;
    public String tagID;
    JsonForAddNoteRequest(String value, String tagId){
        this.tagID = tagId;
        note = value;
    }

    
}