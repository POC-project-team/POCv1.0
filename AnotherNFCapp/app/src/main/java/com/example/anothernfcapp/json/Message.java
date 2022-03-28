package com.example.anothernfcapp.json;

public class Message {
    public String note;
    public String tagID;
    Message(String value, String tagId){
        this.tagID = tagId;
        note = value;
    }

    
}