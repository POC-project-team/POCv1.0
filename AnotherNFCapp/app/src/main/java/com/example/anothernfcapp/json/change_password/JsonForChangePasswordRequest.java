package com.example.anothernfcapp.json.change_password;

public class JsonForChangePasswordRequest {
    private String login;
    private String password;

    public JsonForChangePasswordRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
