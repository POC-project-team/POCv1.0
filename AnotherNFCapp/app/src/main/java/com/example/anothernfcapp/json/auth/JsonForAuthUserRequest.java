package com.example.anothernfcapp.json.auth;

public class JsonForAuthUserRequest {
    String login;
    String password;

    public JsonForAuthUserRequest(String login, String password){
        this.login = login;
        this.password = password;
    }
}
