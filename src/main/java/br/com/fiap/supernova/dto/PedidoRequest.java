package br.com.fiap.supernova.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record PedidoRequest(
        @NotEmpty(message = "O pedido precisa ter pelo menos um item") @Size(max = 50) @Valid List<Item> itens
) {
    public record Item(
            @NotNull(message = "A oferta e obrigatoria") Long ofertaId,
            @NotNull(message = "A quantidade e obrigatoria") @Positive(message = "A quantidade deve ser maior que zero")
            @Digits(integer = 8, fraction = 2) BigDecimal quantidade
    ) {
    }
}
