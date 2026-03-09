package com.desafio.backend.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ResponseErroDto {

    private LocalDateTime dataHora;
    private int status;
    private String mensagem;
    private Map<String, String> detalhes;

    public ResponseErroDto(int status, String mensagem, Map<String, String> detalhes) {
        this.dataHora = LocalDateTime.now();
        this.status = status;
        this.mensagem = mensagem;
        this.detalhes = detalhes;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public int getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Map<String, String> getDetalhes() {
        return detalhes;
    }
}