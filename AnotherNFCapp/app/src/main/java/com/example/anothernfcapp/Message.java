package com.example.anothernfcapp;

public class Message {
    public String note;
    public String tagID;
    Message(String value, String tagId){
        this.tagID = tagId;
        note = value;
    }

    
}