package br.com.fiap.supernova.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistroCompradorRequest(
        @NotBlank(message = "O e-mail e obrigatorio") @Email(message = "E-mail invalido") String email,
        @NotBlank(message = "A senha e obrigatoria") @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres") String senha,
        @NotNull(message = "Os dados do comprador sao obrigatorios") @Valid CompradorRequest comprador
) {
}
