package com.example.anothernfcapp.json.register;

public class JsonForRegisterUserRequest {
    public String login;
    public String password;

    public JsonForRegisterUserRequest(String login, String password){
        this.login = login;
        this.password = password;
    }
}
