package com.example.anothernfcapp.json;

public class JsonForPOSTAuthUser {
    String login;
    String password;

    JsonForPOSTAuthUser(String login, String password){
        this.login = login;
        this.password = password;
    }
}
