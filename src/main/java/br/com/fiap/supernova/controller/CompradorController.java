package br.com.fiap.supernova.controller;

import br.com.fiap.supernova.dto.CompradorRequest;
import br.com.fiap.supernova.dto.CompradorResponse;
import br.com.fiap.supernova.model.TipoComprador;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.service.CompradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compradores")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Compradores", description = "Escolas, restaurantes e feiras")
public class CompradorController {

    private final CompradorService compradorService;

    @GetMapping
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Lista compradores com paginacao (GESTOR)")
    public Page<CompradorResponse> listar(@RequestParam(required = false) TipoComprador tipo,
                                          @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return compradorService.listar(tipo, pageable);
    }

    @GetMapping("/{id}")
    public CompradorResponse buscar(@PathVariable Long id) {
        return compradorService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Cadastra comprador sem usuario de acesso (GESTOR)")
    public CompradorResponse criar(@RequestBody @Valid CompradorRequest request) {
        return compradorService.criar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'COMPRADOR')")
    @Operation(summary = "Atualiza comprador (GESTOR ou o proprio comprador)")
    public CompradorResponse atualizar(@PathVariable Long id, @RequestBody @Valid CompradorRequest request,
                                       @AuthenticationPrincipal Usuario usuario) {
        return compradorService.atualizar(id, request, usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Exclui comprador sem pedidos (GESTOR)")
    public void excluir(@PathVariable Long id) {
        compradorService.excluir(id);
    }
}
