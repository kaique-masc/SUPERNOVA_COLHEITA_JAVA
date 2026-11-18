package br.com.fiap.supernova.service;

import br.com.fiap.supernova.dto.*;
import br.com.fiap.supernova.exception.RegraNegocioException;
import br.com.fiap.supernova.model.Comprador;
import br.com.fiap.supernova.model.Produtor;
import br.com.fiap.supernova.model.Role;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.repository.UsuarioRepository;
import br.com.fiap.supernova.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProdutorService produtorService;
    private final CompradorService compradorService;

    public TokenResponse login(LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        return gerarResposta((Usuario) auth.getPrincipal());
    }

    @Transactional
    public TokenResponse registrarProdutor(RegistroProdutorRequest request) {
        validarEmail(request.email());
        Produtor produtor = produtorService.salvarNovo(request.produtor());

        Usuario usuario = new Usuario(produtor.getNome(), request.email(),
                passwordEncoder.encode(request.senha()), Role.PRODUTOR);
        usuario.setProdutor(produtor);
        return gerarResposta(usuarioRepository.save(usuario));
    }

    @Transactional
    public TokenResponse registrarComprador(RegistroCompradorRequest request) {
        validarEmail(request.email());
        Comprador comprador = compradorService.salvarNovo(request.comprador());

        Usuario usuario = new Usuario(comprador.getNome(), request.email(),
                passwordEncoder.encode(request.senha()), Role.COMPRADOR);
        usuario.setComprador(comprador);
        return gerarResposta(usuarioRepository.save(usuario));
    }

    private void validarEmail(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException("Ja existe um usuario com o e-mail " + email + ".");
        }
    }

    private TokenResponse gerarResposta(Usuario usuario) {
        return new TokenResponse(tokenService.gerarToken(usuario), "Bearer",
                tokenService.dataExpiracao(), UsuarioResponse.from(usuario));
    }
}
