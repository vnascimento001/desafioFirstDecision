package com.desafio.backend.dto;

public class ResponseCadastroUsuarioDto {
	
    private Long id;
    private String nome;
    private String email;

    public ResponseCadastroUsuarioDto() {
    }

    public ResponseCadastroUsuarioDto(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
