package br.com.fiap.supernova.controller;

import br.com.fiap.supernova.dto.*;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacao", description = "Login e cadastro de produtores e compradores")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Faz login e devolve o token JWT")
    public TokenResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/registro/produtor")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra um produtor da agricultura familiar e ja faz login")
    public TokenResponse registrarProdutor(@RequestBody @Valid RegistroProdutorRequest request) {
        return authService.registrarProdutor(request);
    }

    @PostMapping("/registro/comprador")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra uma escola, restaurante ou feira e ja faz login")
    public TokenResponse registrarComprador(@RequestBody @Valid RegistroCompradorRequest request) {
        return authService.registrarComprador(request);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Dados do usuario logado")
    public UsuarioResponse me(@AuthenticationPrincipal Usuario usuario) {
        return UsuarioResponse.from(usuario);
    }
}
