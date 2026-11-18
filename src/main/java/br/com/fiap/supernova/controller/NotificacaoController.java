package br.com.fiap.supernova.controller;

import br.com.fiap.supernova.dto.NotificacaoResponse;
import br.com.fiap.supernova.model.Usuario;
import br.com.fiap.supernova.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notificacoes", description = "Avisos gerados pelos eventos de pedido (RabbitMQ)")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    @Operation(summary = "Notificacoes do usuario logado")
    public Page<NotificacaoResponse> listar(@AuthenticationPrincipal Usuario usuario,
                                            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
                                            Pageable pageable) {
        return notificacaoService.listar(usuario, pageable);
    }

    @PatchMapping("/{id}/lida")
    public NotificacaoResponse marcarComoLida(@AuthenticationPrincipal Usuario usuario, @PathVariable Long id) {
        return notificacaoService.marcarComoLida(usuario, id);
    }
}
