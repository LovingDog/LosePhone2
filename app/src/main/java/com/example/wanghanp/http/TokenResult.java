package com.example.wanghanp.http;

public class TokenResult {
    private String token;

    public TokenResult(String result) {
        this.token = result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "TokenResult{" +
                "token='" + token + '\'' +
                '}';
    }
}
