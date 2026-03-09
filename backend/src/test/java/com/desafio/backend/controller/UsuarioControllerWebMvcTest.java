package com.desafio.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.desafio.backend.dto.RequestCadastroUsuarioDto;
import com.desafio.backend.dto.RequestEdicaoUsuarioDto;
import com.desafio.backend.dto.ResponseCadastroUsuarioDto;
import com.desafio.backend.dto.ResponseEdicaoUsuarioDto;
import com.desafio.backend.exception.BusinessException;
import com.desafio.backend.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UsuarioController.class)
@Import(TratadorErrosController.class)
class UsuarioControllerWebMvcTest {

    private static final String API_USUARIOS = "/api/usuarios";
	private static final String USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";

	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void cadastrarDeveRetornarCreatedQuandoDadosValidos() throws Exception {
        RequestCadastroUsuarioDto req = requisicao("Ana", "ana@email.com", "senha123", "senha123");
        ResponseCadastroUsuarioDto response = new ResponseCadastroUsuarioDto(1L, "Ana", "ana@email.com");

        when(usuarioService.cadastrarUsuario(any(RequestCadastroUsuarioDto.class))).thenReturn(response);

        mockMvc.perform(post(API_USUARIOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Ana"))
                .andExpect(jsonPath("$.email").value("ana@email.com"));
    }

    @Test
    void cadastrarDeveRetornarBadRequestQuandoBodyForInvalido() throws Exception {
        RequestCadastroUsuarioDto req = requisicao("An", "invalido", "123", "456");

        mockMvc.perform(post(API_USUARIOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalhes.nome").exists())
                .andExpect(jsonPath("$.detalhes.email").exists());
    }

    @Test
    void listarDeveRetornarOkComPaginaUsuarios() throws Exception {

        List<ResponseCadastroUsuarioDto> usuarios = List.of(
                new ResponseCadastroUsuarioDto(1L, "Ana", "ana@email.com"),
                new ResponseCadastroUsuarioDto(2L, "Bruno", "bruno@email.com")
        );

        Page<ResponseCadastroUsuarioDto> pagina =
                new PageImpl<>(usuarios, PageRequest.of(0, 10), usuarios.size());

        when(usuarioService.listarUsuarios(any(Pageable.class), nullable(String.class))).thenReturn(pagina);

        mockMvc.perform(get(API_USUARIOS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Ana"))
                .andExpect(jsonPath("$.content[1].email").value("bruno@email.com"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void editarDeveRetornarOkQuandoDadosValidos() throws Exception {
        RequestEdicaoUsuarioDto req = requisicaoEdicao("Ana", "ana@email.com", "senha123", "senha123");
        ResponseEdicaoUsuarioDto response = new ResponseEdicaoUsuarioDto(1L, "Ana", "ana@email.com");

        when(usuarioService.editarUsuario(eq(1L), any(RequestEdicaoUsuarioDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Ana"));
    }

    @Test
    void editarDeveRetornarNotFoundQuandoServiceLancarBusinessException() throws Exception {
        RequestEdicaoUsuarioDto req = requisicaoEdicao("Ana", "ana@email.com", "senha123", "senha123");
        when(usuarioService.editarUsuario(eq(1L), any(RequestEdicaoUsuarioDto.class)))
                .thenThrow(new BusinessException(USUARIO_NAO_ENCONTRADO, HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value(USUARIO_NAO_ENCONTRADO));
    }

    @Test
    void deletarDeveRetornarNoContentQuandoSucesso() throws Exception {
        doNothing().when(usuarioService).deletarUsuario(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletarDeveRetornarNotFoundQuandoServiceLancarBusinessException() throws Exception {
        doThrow(new BusinessException(USUARIO_NAO_ENCONTRADO, HttpStatus.NOT_FOUND))
                .when(usuarioService).deletarUsuario(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value(USUARIO_NAO_ENCONTRADO));
    }

    private RequestCadastroUsuarioDto requisicao(String nome, String email, String senha, String confirmacao) {
        RequestCadastroUsuarioDto dto = new RequestCadastroUsuarioDto();
        dto.setNome(nome);
        dto.setEmail(email);
        dto.setSenha(senha);
        dto.setConfirmacaoSenha(confirmacao);
        return dto;
    }

    private RequestEdicaoUsuarioDto requisicaoEdicao(String nome, String email, String senha, String confirmacao) {
        RequestEdicaoUsuarioDto dto = new RequestEdicaoUsuarioDto();
        dto.setNome(nome);
        dto.setEmail(email);
        dto.setSenha(senha);
        dto.setConfirmacaoSenha(confirmacao);
        return dto;
    }
}
