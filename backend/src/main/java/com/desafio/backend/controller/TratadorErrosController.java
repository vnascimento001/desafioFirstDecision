package com.desafio.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.desafio.backend.dto.ResponseErroDto;
import com.desafio.backend.exception.BusinessException;
import com.desafio.backend.exception.TechnicalException;

@RestControllerAdvice
public class TratadorErrosController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TratadorErrosController.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseErroDto> tratarErrosValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> detalhes = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(erro ->
                detalhes.put(erro.getField(), erro.getDefaultMessage())
        );

        ResponseErroDto resposta = new ResponseErroDto(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validacao",
                detalhes
        );

        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseErroDto> tratarErroRegraNegocio(BusinessException ex) {
        LOGGER.warn("Business error [{}]", ex.getMessage());

        ResponseErroDto resposta = new ResponseErroDto(
                ex.getStatus().value(),
                ex.getMessage(),
                ex.getDetails()
        );

        return ResponseEntity.status(ex.getStatus()).body(resposta);
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ResponseErroDto> tratarErroTecnico(TechnicalException ex) {
        LOGGER.error("Technical error [{}]: {}", ex.getMessage(), ex);

        ResponseErroDto resposta = new ResponseErroDto(
                ex.getStatus().value(),
                "Erro interno no servidor",
                ex.getDetails()
        );

        return ResponseEntity.status(ex.getStatus()).body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseErroDto> tratarErroGenerico(Exception ex) {
        LOGGER.error("Unexpected error", ex);

        ResponseErroDto resposta = new ResponseErroDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno no servidor",
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }
}
