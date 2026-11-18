package br.com.fiap.supernova.controller;

import br.com.fiap.supernova.dto.PedidoRequest;
import br.com.fiap.supernova.dto.PedidoResponse;
import br.com.fiap.supernova.dto.StatusPedidoRequest;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pedidos", description = "Compras feitas pelos compradores")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('COMPRADOR')")
    @Operation(summary = "Cria pedido (COMPRADOR). Valida estoque, validade e CAF do produtor para escolas")
    public PedidoResponse criar(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid PedidoRequest request) {
        return pedidoService.criar(usuario, request);
    }

    @GetMapping
    @Operation(summary = "Lista pedidos: comprador ve os seus, produtor ve os que tem produtos dele, gestor ve todos")
    public Page<PedidoResponse> listar(@AuthenticationPrincipal Usuario usuario,
                                       @PageableDefault(size = 20, sort = "dataPedido", direction = Sort.Direction.DESC)
                                       Pageable pageable) {
        return pedidoService.listar(usuario, pageable);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@AuthenticationPrincipal Usuario usuario, @PathVariable Long id) {
        return pedidoService.buscar(usuario, id);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Muda o status: produtor confirma/entrega, comprador cancela, gestor faz tudo")
    public PedidoResponse atualizarStatus(@AuthenticationPrincipal Usuario usuario, @PathVariable Long id,
                                          @RequestBody @Valid StatusPedidoRequest request) {
        return pedidoService.atualizarStatus(usuario, id, request.status());
    }
}
