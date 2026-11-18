package br.com.fiap.supernova.controller;

import br.com.fiap.supernova.dto.CardapioRequest;
import br.com.fiap.supernova.dto.CardapioResponse;
import br.com.fiap.supernova.dto.PedidoResponse;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.service.CardapioService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cardapios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cardapio inteligente (IA)",
        description = "A IA monta o cardapio da semana com o que os produtores da regiao tem disponivel (COMPRADOR)")
public class CardapioController {

    private final CardapioService cardapioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Gera um cardapio com IA a partir das ofertas da regiao, do numero de alunos e do orcamento")
    public CardapioResponse gerar(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid CardapioRequest request) {
        return cardapioService.gerar(usuario, request);
    }

    @GetMapping
    public Page<CardapioResponse> listar(@AuthenticationPrincipal Usuario usuario,
                                         @PageableDefault(size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC)
                                         Pageable pageable) {
        return cardapioService.listar(usuario, pageable);
    }

    @GetMapping("/{id}")
    public CardapioResponse buscar(@AuthenticationPrincipal Usuario usuario, @PathVariable Long id) {
        return cardapioService.buscar(usuario, id);
    }

    @PostMapping("/{id}/pedido")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Transforma a lista de compras do cardapio em um pedido")
    public PedidoResponse gerarPedido(@AuthenticationPrincipal Usuario usuario, @PathVariable Long id) {
        return cardapioService.gerarPedido(usuario, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@AuthenticationPrincipal Usuario usuario, @PathVariable Long id) {
        cardapioService.excluir(usuario, id);
    }
}
