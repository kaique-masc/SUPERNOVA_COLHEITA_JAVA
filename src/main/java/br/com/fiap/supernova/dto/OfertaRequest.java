package br.com.fiap.supernova.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfertaRequest(
        @NotNull(message = "O produto e obrigatorio") Long produtoId,
        @NotNull(message = "O preco e obrigatorio") @Positive(message = "O preco deve ser maior que zero")
        @Digits(integer = 8, fraction = 2) BigDecimal precoUnitario,
        @NotNull(message = "A quantidade e obrigatoria") @PositiveOrZero(message = "A quantidade nao pode ser negativa")
        @Digits(integer = 8, fraction = 2) BigDecimal qtdDisponivel,
        @NotNull(message = "A data de colheita e obrigatoria") @PastOrPresent(message = "A colheita nao pode ser no futuro") LocalDate dataColheita,
        @NotNull(message = "A data de validade e obrigatoria") LocalDate dataValidade
) {
}
