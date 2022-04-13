package com.example.anothernfcapp.utility;

public class StaticVariables {
    public static String ipServerUrl = "http://217.25.88.71:60494/";
    public static String JWT;
    public static String tagId;

    public static void setTagId(String tagId) {
        StaticVariables.tagId = tagId;
    }

    public static void setJWT(String JWT){
        StaticVariables.JWT = JWT;
    }
}
