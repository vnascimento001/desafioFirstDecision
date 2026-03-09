package com.desafio.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.desafio.backend.dto.RequestCadastroUsuarioDto;
import com.desafio.backend.dto.RequestEdicaoUsuarioDto;
import com.desafio.backend.dto.ResponseCadastroUsuarioDto;
import com.desafio.backend.dto.ResponseEdicaoUsuarioDto;
import com.desafio.backend.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@Validated
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<ResponseCadastroUsuarioDto> cadastrar(@Valid @RequestBody RequestCadastroUsuarioDto requisicao) {
    	ResponseCadastroUsuarioDto resposta = usuarioService.cadastrarUsuario(requisicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseCadastroUsuarioDto>> listarUsuarios(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable,
            @RequestParam(required = false) String busca
    ) {
        Page<ResponseCadastroUsuarioDto> usuarios = usuarioService.listarUsuarios(pageable, busca);
        return ResponseEntity.ok(usuarios);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ResponseEdicaoUsuarioDto> editar(@PathVariable Long id, @Valid @RequestBody RequestEdicaoUsuarioDto requisicao) {
    	ResponseEdicaoUsuarioDto resposta = usuarioService.editarUsuario(id, requisicao);
        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
