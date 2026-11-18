package br.com.fiap.supernova.dto;

import br.com.fiap.supernova.model.TipoComprador;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CompradorRequest(
        @NotBlank(message = "O nome e obrigatorio") @Size(max = 120) String nome,
        @NotNull(message = "O tipo e obrigatorio (ESCOLA, RESTAURANTE ou FEIRA)") TipoComprador tipo,
        @NotBlank(message = "O CNPJ e obrigatorio") @Pattern(regexp = "\\d{14}", message = "O CNPJ deve ter 14 digitos, sem pontos") String cnpj,
        @NotBlank(message = "O municipio e obrigatorio") @Size(max = 80) String municipio,
        @NotBlank(message = "A UF e obrigatoria") @Pattern(regexp = "[A-Z]{2}", message = "A UF deve ter 2 letras maiusculas") String uf,
        @PositiveOrZero(message = "A verba nao pode ser negativa") BigDecimal verbaPnaeAnual,
        @Email(message = "E-mail invalido") String email
) {
}
