package br.com.fiap.supernova.dto;

import br.com.fiap.supernova.model.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record StatusPedidoRequest(
        @NotNull(message = "O status e obrigatorio (CONFIRMADO, ENTREGUE ou CANCELADO)") StatusPedido status
) {
}
