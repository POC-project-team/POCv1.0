package com.example.anothernfcapp.json.add_notes;

public class JsonForAddNoteRequest {
    public String note;
    public String tagID;
    public JsonForAddNoteRequest(String value, String tagId){
        this.tagID = tagId;
        note = value;
    }

    
}