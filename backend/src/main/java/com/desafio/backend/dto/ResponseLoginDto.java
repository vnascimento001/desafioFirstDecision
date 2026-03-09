package com.desafio.backend.dto;

public class ResponseLoginDto {

    private String token;

    public ResponseLoginDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
