package com.desafio.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.desafio.backend.dto.RequestCadastroUsuarioDto;
import com.desafio.backend.dto.RequestEdicaoUsuarioDto;
import com.desafio.backend.dto.ResponseCadastroUsuarioDto;
import com.desafio.backend.dto.ResponseEdicaoUsuarioDto;
import com.desafio.backend.entity.Usuario;
import com.desafio.backend.exception.BusinessException;
import com.desafio.backend.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final String ERRO_CREDENCIAIS_INVALIDAS = "Credenciais inválidas";
    private static final String ERRO_EMAIL_JA_EXISTE = "Email já cadastrado";
    private static final String ERRO_CONFIRMACAO_SENHA = "A confirmação de senha não confere";
    private static final String ERRO_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseCadastroUsuarioDto cadastrarUsuario(RequestCadastroUsuarioDto requisicao) {
        validarRequisicaoDeCadastro(requisicao);
        return toResponseCadastroUsuarioDto(salvarUsuario(criarUsuario(requisicao)));
    }

    public Page<ResponseCadastroUsuarioDto> listarUsuarios(Pageable pageable, String busca) {
        int tamanhoPagina = Math.min(pageable.getPageSize(), 10);

        Pageable pageableAjustado = PageRequest.of(
                pageable.getPageNumber(),
                tamanhoPagina,
                pageable.getSort()
        );

        String termoBusca = busca != null ? busca.trim() : "";
        Page<Usuario> usuarios = StringUtils.hasText(termoBusca)
                ? usuarioRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(termoBusca, termoBusca, pageableAjustado)
                : usuarioRepository.findAll(pageableAjustado);

        return usuarios.map(this::toResponseCadastroUsuarioDto);
    }

    public ResponseEdicaoUsuarioDto editarUsuario(Long id, RequestEdicaoUsuarioDto requisicao) {
        Usuario usuarioExistente = buscarUsuarioPorIdOuFalhar(id);
        validarRequisicaoDeEdicao(usuarioExistente, requisicao);

        atualizarUsuario(usuarioExistente, requisicao);
        Usuario usuarioAtualizado = salvarUsuario(usuarioExistente);

        return toResponseEdicaoUsuarioDto(usuarioAtualizado);
    }

    public void deletarUsuario(Long id) {
        Usuario usuario = buscarUsuarioPorIdOuFalhar(id);
        usuarioRepository.delete(usuario);
    }

    protected void validarRequisicaoDeCadastro(RequestCadastroUsuarioDto requisicao) {
        validarConfirmacaoDeSenha(requisicao);
        validarEmailDisponivelParaCadastro(requisicao.getEmail());
    }

    protected void validarRequisicaoDeEdicao(Usuario usuarioAtual, RequestEdicaoUsuarioDto requisicao) {
        validarConfirmacaoDeSenha(requisicao);
        validarEmailDisponivelParaEdicao(usuarioAtual, requisicao.getEmail());
    }

    protected void validarConfirmacaoDeSenha(RequestCadastroUsuarioDto requisicao) {
        if (!requisicao.getSenha().equals(requisicao.getConfirmacaoSenha())) {
            throw new BusinessException(ERRO_CONFIRMACAO_SENHA, HttpStatus.BAD_REQUEST);
        }
    }

    protected void validarConfirmacaoDeSenha(RequestEdicaoUsuarioDto requisicao) {
        if (!requisicao.getSenha().equals(requisicao.getConfirmacaoSenha())) {
            throw new BusinessException(ERRO_CONFIRMACAO_SENHA, HttpStatus.BAD_REQUEST);
        }
    }

    protected void validarEmailDisponivelParaCadastro(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new BusinessException(ERRO_EMAIL_JA_EXISTE, HttpStatus.CONFLICT);
        }
    }

    protected void validarEmailDisponivelParaEdicao(Usuario usuarioAtual, String novoEmail) {
        boolean alterouEmail = !usuarioAtual.getEmail().equals(novoEmail);

        if (alterouEmail && usuarioRepository.existsByEmail(novoEmail)) {
            throw new BusinessException(ERRO_EMAIL_JA_EXISTE, HttpStatus.CONFLICT);
        }
    }

    protected Usuario buscarUsuarioPorEmailOuFalhar(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ERRO_CREDENCIAIS_INVALIDAS, HttpStatus.UNAUTHORIZED));
    }

    protected Usuario buscarUsuarioPorIdOuFalhar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ERRO_USUARIO_NAO_ENCONTRADO, HttpStatus.NOT_FOUND));
    }

    protected void validarSenhaInformada(String senhaInformada, String senhaCriptografada) {
        if (!passwordEncoder.matches(senhaInformada, senhaCriptografada)) {
            throw new BusinessException(ERRO_CREDENCIAIS_INVALIDAS, HttpStatus.UNAUTHORIZED);
        }
    }

    protected Usuario criarUsuario(RequestCadastroUsuarioDto requisicao) {
        return new Usuario(requisicao.getNome(), requisicao.getEmail(), criptografarSenha(requisicao.getSenha()));
    }

    protected void atualizarUsuario(Usuario usuario, RequestEdicaoUsuarioDto requisicao) {
        usuario.setNome(requisicao.getNome());
        usuario.setEmail(requisicao.getEmail());
        usuario.setSenha(criptografarSenha(requisicao.getSenha()));
    }

    protected String criptografarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

    protected Usuario salvarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    protected ResponseCadastroUsuarioDto toResponseCadastroUsuarioDto(Usuario usuario) {
        return new ResponseCadastroUsuarioDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }

    protected ResponseEdicaoUsuarioDto toResponseEdicaoUsuarioDto(Usuario usuario) {
        return new ResponseEdicaoUsuarioDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}
