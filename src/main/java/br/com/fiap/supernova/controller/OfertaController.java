package br.com.fiap.supernova.controller;

import br.com.fiap.supernova.dto.OfertaRequest;
import br.com.fiap.supernova.dto.OfertaResponse;
import br.com.fiap.supernova.model.StatusOferta;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.service.OfertaService;
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
import org.springdoc.core.annotations.ParameterObject;



@RestController
@RequestMapping("/ofertas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ofertas", description = "Produtos que os produtores tem a venda")
public class OfertaController {

    private final OfertaService ofertaService;

    @GetMapping
    @Operation(summary = "Lista ofertas com paginacao e filtros")
    public Page<OfertaResponse> listar(
            @RequestParam(required = false) StatusOferta status,
            @RequestParam(required = false) Long produtoId,
            @RequestParam(required = false) Long produtorId,
            @RequestParam(required = false) String uf,
            @ParameterObject @PageableDefault(size = 20, sort = "dataValidade") Pageable pageable) {

        return ofertaService.listar(status, produtoId, produtorId, uf, pageable);
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('PRODUTOR')")
    @Operation(summary = "Ofertas do produtor logado (PRODUTOR)")
    public Page<OfertaResponse> minhas(@AuthenticationPrincipal Usuario usuario,
                                       @ParameterObject @PageableDefault(size = 20, sort = "dataValidade") Pageable pageable) {
        return ofertaService.listarDoProdutor(usuario, pageable);
    }

    @GetMapping("/{id}")
    public OfertaResponse buscar(@PathVariable Long id) {
        return ofertaService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PRODUTOR')")
    @Operation(summary = "Cadastra oferta (PRODUTOR)")
    public OfertaResponse criar(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid OfertaRequest request) {
        return ofertaService.criar(usuario, request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRODUTOR', 'GESTOR')")
    @Operation(summary = "Atualiza oferta (dono da oferta ou GESTOR)")
    public OfertaResponse atualizar(@AuthenticationPrincipal Usuario usuario, @PathVariable Long id,
                                    @RequestBody @Valid OfertaRequest request) {
        return ofertaService.atualizar(usuario, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('PRODUTOR', 'GESTOR')")
    @Operation(summary = "Cancela oferta (dono da oferta ou GESTOR)")
    public void cancelar(@AuthenticationPrincipal Usuario usuario, @PathVariable Long id) {
        ofertaService.cancelar(usuario, id);
    }
}
