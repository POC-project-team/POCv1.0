package com.example.anothernfcapp.screens;

public class StaticVariables {
    static String ipServerUrl = "http://217.25.88.71:60494/";
    static String JWT;
    static String tagId;

    public static void setTagId(String tagId) {
        StaticVariables.tagId = tagId;
    }

    static void setJWT(String JWT){
        StaticVariables.JWT = JWT;
    }
}
