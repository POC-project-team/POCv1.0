package com.example.anothernfcapp.json;

public class JsonForResponseAuthUser {
    String token;

    JsonForResponseAuthUser(String token){
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
