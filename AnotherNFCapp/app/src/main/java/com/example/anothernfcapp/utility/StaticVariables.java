package com.example.anothernfcapp.utility;

public class StaticVariables {
    public static final String ipServerUrl = "http://217.25.88.71:60494/";
    public static String JWT;
    public static String tagId;
    public static String login;
    public static String password;
    public static final String superUser = "poc";
    public final static String superTagId = "0x040c446a287381";

    public static void setTagId(String tagId) {
        StaticVariables.tagId = tagId;
    }

    public static void setJWT(String JWT){
        StaticVariables.JWT = JWT;
    }

    public static void setLogin(String login) {
        StaticVariables.login = login;
    }

    public static void setPassword(String password){
        StaticVariables.password = password;
    }
}
