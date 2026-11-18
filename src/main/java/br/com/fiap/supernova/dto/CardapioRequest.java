package br.com.fiap.supernova.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CardapioRequest(
        @NotNull(message = "O numero de alunos e obrigatorio") @Min(value = 1, message = "Minimo de 1 aluno")
        @Max(value = 5000, message = "Maximo de 5000 alunos") Integer numeroAlunos,
        @NotNull(message = "O orcamento e obrigatorio") @Positive(message = "O orcamento deve ser maior que zero")
        @Digits(integer = 10, fraction = 2) BigDecimal orcamento,
        @Min(value = 1, message = "Minimo de 1 dia") @Max(value = 5, message = "Maximo de 5 dias") Integer diasLetivos,
        @Size(max = 500, message = "As restricoes podem ter ate 500 caracteres") String restricoes
) {
}
