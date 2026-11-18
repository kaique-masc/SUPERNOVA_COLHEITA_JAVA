package br.com.fiap.supernova.controller;

import br.com.fiap.supernova.dto.ProdutorRequest;
import br.com.fiap.supernova.dto.ProdutorResponse;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.service.ProdutorService;
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
@RequestMapping("/produtores")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Produtores", description = "Produtores da agricultura familiar")
public class ProdutorController {

    private final ProdutorService produtorService;

    @GetMapping
    @Operation(summary = "Lista produtores com paginacao, opcionalmente filtrando pelo nome")
    public Page<ProdutorResponse> listar(@RequestParam(required = false) String nome,
                                         @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return produtorService.listar(nome, pageable);
    }

    @GetMapping("/{id}")
    public ProdutorResponse buscar(@PathVariable Long id) {
        return produtorService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Cadastra produtor sem usuario de acesso (GESTOR)")
    public ProdutorResponse criar(@RequestBody @Valid ProdutorRequest request) {
        return produtorService.criar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR', 'PRODUTOR')")
    @Operation(summary = "Atualiza produtor (GESTOR ou o proprio produtor)")
    public ProdutorResponse atualizar(@PathVariable Long id, @RequestBody @Valid ProdutorRequest request,
                                      @AuthenticationPrincipal Usuario usuario) {
        return produtorService.atualizar(id, request, usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Inativa produtor (GESTOR)")
    public void inativar(@PathVariable Long id) {
        produtorService.inativar(id);
    }
}
