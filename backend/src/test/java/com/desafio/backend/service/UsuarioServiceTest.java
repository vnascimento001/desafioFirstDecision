package com.desafio.backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.desafio.backend.dto.RequestCadastroUsuarioDto;
import com.desafio.backend.dto.RequestEdicaoUsuarioDto;
import com.desafio.backend.dto.ResponseCadastroUsuarioDto;
import com.desafio.backend.dto.ResponseEdicaoUsuarioDto;
import com.desafio.backend.entity.Usuario;
import com.desafio.backend.exception.BusinessException;
import com.desafio.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void cadastrarUsuarioDeveCadastrarQuandoRequisicaoForValida() {
        RequestCadastroUsuarioDto req = requisicao("Ana", "ana@email.com", "senha123", "senha123");
        Usuario salvo = usuarioComId(10L, "Ana", "ana@email.com", "senha-hash");

        when(usuarioRepository.existsByEmail("ana@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senha-hash");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(salvo);

        ResponseCadastroUsuarioDto response = usuarioService.cadastrarUsuario(req);

        assertEquals(10L, response.getId());
        assertEquals("Ana", response.getNome());
        assertEquals("ana@email.com", response.getEmail());
        verify(usuarioRepository).existsByEmail("ana@email.com");
        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void cadastrarUsuarioDeveLancarErroQuandoConfirmacaoSenhaForDiferente() {
        RequestCadastroUsuarioDto req = requisicao("Ana", "ana@email.com", "senha123", "outra");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.cadastrarUsuario(req));

        assertEquals("A confirmacao de senha nao confere", ex.getMessage());
        verify(usuarioRepository, never()).existsByEmail(any());
        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void cadastrarUsuarioDeveLancarErroQuandoEmailJaExistir() {
        RequestCadastroUsuarioDto req = requisicao("Ana", "ana@email.com", "senha123", "senha123");
        when(usuarioRepository.existsByEmail("ana@email.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.cadastrarUsuario(req));

        assertEquals("Ja existe um usuario com este e-mail", ex.getMessage());
        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void listarUsuariosDeveRetornarPaginaVaziaQuandoNaoHouverUsuarios() {
        Pageable pageable = PageRequest.of(0, 10);

        when(usuarioRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<ResponseCadastroUsuarioDto> response = usuarioService.listarUsuarios(pageable, "");

        assertTrue(response.isEmpty());
        assertTrue(response.getContent().isEmpty());
    }

    @Test
    void listarUsuariosDeveMapearUsuariosParaDto() {
        Pageable pageable = PageRequest.of(0, 10);

        Usuario u1 = usuarioComId(1L, "Ana", "ana@email.com", "h1");
        Usuario u2 = usuarioComId(2L, "Bruno", "bruno@email.com", "h2");

        Page<Usuario> paginaUsuarios = new PageImpl<>(List.of(u1, u2), pageable, 2);

        when(usuarioRepository.findAll(pageable)).thenReturn(paginaUsuarios);

        Page<ResponseCadastroUsuarioDto> response = usuarioService.listarUsuarios(pageable, "");

        assertEquals(2, response.getTotalElements());
        assertEquals(2, response.getContent().size());
        assertEquals("Ana", response.getContent().get(0).getNome());
        assertEquals("bruno@email.com", response.getContent().get(1).getEmail());
    }

    @Test
    void editarUsuarioDeveEditarSemTrocaDeEmail() {
        Usuario existente = usuarioComId(5L, "Ana", "ana@email.com", "old");
        RequestEdicaoUsuarioDto req = requisicaoEdicao("Ana Nova", "ana@email.com", "nova", "nova");

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("nova")).thenReturn("nova-hash");
        when(usuarioRepository.save(existente)).thenReturn(existente);

        ResponseEdicaoUsuarioDto response = usuarioService.editarUsuario(5L, req);

        assertEquals(5L, response.getId());
        assertEquals("Ana Nova", existente.getNome());
        assertEquals("ana@email.com", existente.getEmail());
        assertEquals("nova-hash", existente.getSenha());
        verify(usuarioRepository, never()).existsByEmail(any());
    }

    @Test
    void editarUsuarioDeveEditarComTrocaDeEmailDisponivel() {
        Usuario existente = usuarioComId(6L, "Ana", "ana@email.com", "old");
        RequestEdicaoUsuarioDto req = requisicaoEdicao("Ana", "novo@email.com", "nova", "nova");

        when(usuarioRepository.findById(6L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByEmail("novo@email.com")).thenReturn(false);
        when(passwordEncoder.encode("nova")).thenReturn("nova-hash");
        when(usuarioRepository.save(existente)).thenReturn(existente);

        ResponseEdicaoUsuarioDto response = usuarioService.editarUsuario(6L, req);

        assertEquals("novo@email.com", response.getEmail());
        verify(usuarioRepository, times(1)).existsByEmail("novo@email.com");
    }

    @Test
    void editarUsuarioDeveLancarErroQuandoNovoEmailJaExistir() {
        Usuario existente = usuarioComId(7L, "Ana", "ana@email.com", "old");
        RequestEdicaoUsuarioDto req = requisicaoEdicao("Ana", "outro@email.com", "nova", "nova");

        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByEmail("outro@email.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.editarUsuario(7L, req));

        assertEquals("Ja existe um usuario com este e-mail", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void editarUsuarioDeveLancarErroQuandoUsuarioNaoExistir() {
        RequestEdicaoUsuarioDto req = requisicaoEdicao("Ana", "ana@email.com", "nova", "nova");
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.editarUsuario(99L, req));

        assertEquals("Usuario nao encontrado", ex.getMessage());
    }

    @Test
    void editarUsuarioDeveLancarErroQuandoConfirmacaoSenhaForInvalida() {
        Usuario existente = usuarioComId(8L, "Ana", "ana@email.com", "old");
        RequestEdicaoUsuarioDto req = requisicaoEdicao("Ana", "ana@email.com", "nova", "errada");

        when(usuarioRepository.findById(8L)).thenReturn(Optional.of(existente));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.editarUsuario(8L, req));

        assertEquals("A confirmacao de senha nao confere", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deletarUsuarioDeveRemoverQuandoUsuarioExistir() {
        Usuario existente = usuarioComId(11L, "Ana", "ana@email.com", "old");
        when(usuarioRepository.findById(11L)).thenReturn(Optional.of(existente));

        usuarioService.deletarUsuario(11L);

        verify(usuarioRepository).delete(existente);
    }

    @Test
    void deletarUsuarioDeveLancarErroQuandoUsuarioNaoExistir() {
        when(usuarioRepository.findById(12L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.deletarUsuario(12L));

        assertEquals("Usuario nao encontrado", ex.getMessage());
        verify(usuarioRepository, never()).delete(any());
    }

    @Test
    void validarRequisicaoDeCadastroDevePassarQuandoDadosValidos() {
        RequestCadastroUsuarioDto req = requisicao("Ana", "ana@email.com", "senha", "senha");
        when(usuarioRepository.existsByEmail("ana@email.com")).thenReturn(false);

        assertDoesNotThrow(() -> usuarioService.validarRequisicaoDeCadastro(req));
    }

    @Test
    void validarRequisicaoDeCadastroDeveLancarErroQuandoEmailExistir() {
        RequestCadastroUsuarioDto req = requisicao("Ana", "ana@email.com", "senha", "senha");
        when(usuarioRepository.existsByEmail("ana@email.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.validarRequisicaoDeCadastro(req));

        assertEquals("Ja existe um usuario com este e-mail", ex.getMessage());
    }

    @Test
    void validarRequisicaoDeEdicaoDeveLancarErroQuandoNovoEmailJaExistir() {
        Usuario atual = usuarioComId(1L, "Ana", "ana@email.com", "hash");
        RequestEdicaoUsuarioDto req = requisicaoEdicao("Ana", "novo@email.com", "senha", "senha");
        when(usuarioRepository.existsByEmail("novo@email.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.validarRequisicaoDeEdicao(atual, req));

        assertEquals("Ja existe um usuario com este e-mail", ex.getMessage());
    }

    @Test
    void validarConfirmacaoDeSenhaDeveLancarErroQuandoSenhasDiferentes() {
        RequestCadastroUsuarioDto req = requisicao("Ana", "ana@email.com", "senha", "outra");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.validarConfirmacaoDeSenha(req));

        assertEquals("A confirmacao de senha nao confere", ex.getMessage());
    }

    @Test
    void validarEmailDisponivelParaCadastroDeveLancarErroQuandoEmailExistir() {
        when(usuarioRepository.existsByEmail("ana@email.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.validarEmailDisponivelParaCadastro("ana@email.com"));

        assertEquals("Ja existe um usuario com este e-mail", ex.getMessage());
    }

    @Test
    void validarEmailDisponivelParaEdicaoNaoDeveConsultarRepositorioQuandoEmailNaoMudar() {
        Usuario atual = usuarioComId(1L, "Ana", "ana@email.com", "hash");

        assertDoesNotThrow(() -> usuarioService.validarEmailDisponivelParaEdicao(atual, "ana@email.com"));

        verify(usuarioRepository, never()).existsByEmail(any());
    }

    @Test
    void buscarUsuarioPorEmailOuFalharDeveRetornarUsuarioQuandoExistir() {
        Usuario usuario = usuarioComId(1L, "Ana", "ana@email.com", "hash");
        when(usuarioRepository.findByEmail("ana@email.com")).thenReturn(Optional.of(usuario));

        Usuario response = usuarioService.buscarUsuarioPorEmailOuFalhar("ana@email.com");

        assertEquals(usuario, response);
    }

    @Test
    void buscarUsuarioPorEmailOuFalharDeveLancarErroQuandoNaoExistir() {
        when(usuarioRepository.findByEmail("ana@email.com")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.buscarUsuarioPorEmailOuFalhar("ana@email.com"));

        assertEquals("Credenciais invalidas", ex.getMessage());
    }

    @Test
    void buscarUsuarioPorIdOuFalharDeveRetornarUsuarioQuandoExistir() {
        Usuario usuario = usuarioComId(1L, "Ana", "ana@email.com", "hash");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario response = usuarioService.buscarUsuarioPorIdOuFalhar(1L);

        assertEquals(usuario, response);
    }

    @Test
    void buscarUsuarioPorIdOuFalharDeveLancarErroQuandoNaoExistir() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.buscarUsuarioPorIdOuFalhar(1L));

        assertEquals("Usuario nao encontrado", ex.getMessage());
    }

    @Test
    void validarSenhaInformadaDeveLancarErroQuandoNaoBater() {
        when(passwordEncoder.matches("informada", "hash")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> usuarioService.validarSenhaInformada("informada", "hash"));

        assertEquals("Credenciais invalidas", ex.getMessage());
    }
    
    @Test
    void validarSenhaInformadaNaoDeveLancarErroQuandoBater() {
        when(passwordEncoder.matches("informada", "informada")).thenReturn(true);

        assertDoesNotThrow(() -> usuarioService.validarSenhaInformada("informada", "informada"));
    }

    @Test
    void criarUsuarioDeveCriarEntidadeComSenhaCriptografada() {
        RequestCadastroUsuarioDto req = requisicao("Ana", "ana@email.com", "senha", "senha");
        when(passwordEncoder.encode("senha")).thenReturn("hash");

        Usuario usuario = usuarioService.criarUsuario(req);

        assertEquals("Ana", usuario.getNome());
        assertEquals("ana@email.com", usuario.getEmail());
        assertEquals("hash", usuario.getSenha());
    }

    @Test
    void atualizarUsuarioDeveAtualizarDadosDaEntidade() {
        Usuario usuario = usuarioComId(1L, "Antigo", "antigo@email.com", "old");
        RequestEdicaoUsuarioDto req = requisicaoEdicao("Novo", "novo@email.com", "senha", "senha");
        when(passwordEncoder.encode("senha")).thenReturn("hash");

        usuarioService.atualizarUsuario(usuario, req);

        assertEquals("Novo", usuario.getNome());
        assertEquals("novo@email.com", usuario.getEmail());
        assertEquals("hash", usuario.getSenha());
    }

    @Test
    void criptografarSenhaDeveDelegarParaPasswordEncoder() {
        when(passwordEncoder.encode("senha")).thenReturn("hash");

        String hash = usuarioService.criptografarSenha("senha");

        assertEquals("hash", hash);
    }

    @Test
    void salvarUsuarioDeveDelegarParaRepositorio() {
        Usuario usuario = usuarioComId(1L, "Ana", "ana@email.com", "hash");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario salvo = usuarioService.salvarUsuario(usuario);

        assertEquals(usuario, salvo);
    }

    @Test
    void toResponseCadastroUsuarioDtoDeveMapearCorretamente() {
        Usuario usuario = usuarioComId(1L, "Ana", "ana@email.com", "hash");

        ResponseCadastroUsuarioDto dto = usuarioService.toResponseCadastroUsuarioDto(usuario);

        assertEquals(1L, dto.getId());
        assertEquals("Ana", dto.getNome());
        assertEquals("ana@email.com", dto.getEmail());
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

    private Usuario usuarioComId(Long id, String nome, String email, String senha) {
        Usuario usuario = new Usuario(nome, email, senha);
        setId(usuario, id);
        return usuario;
    }

    private void setId(Usuario usuario, Long id) {
        try {
            Field field = Usuario.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(usuario, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


