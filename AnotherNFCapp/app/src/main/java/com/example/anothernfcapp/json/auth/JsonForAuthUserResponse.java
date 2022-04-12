package com.example.anothernfcapp.json.auth;

public class JsonForAuthUserResponse {
    String token;

    JsonForAuthUserResponse(String token){
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return token;
    }
}
