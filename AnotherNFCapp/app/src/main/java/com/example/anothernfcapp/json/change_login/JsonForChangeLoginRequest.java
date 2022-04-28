package com.example.anothernfcapp.json.change_login;

public class JsonForChangeLoginRequest {
    private String login;
    private String password;

    public JsonForChangeLoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
