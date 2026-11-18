package br.com.fiap.supernova.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProdutorRequest(
        @NotBlank(message = "O nome e obrigatorio") @Size(max = 100) String nome,
        @NotBlank(message = "O CPF e obrigatorio") @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 digitos, sem pontos") String cpf,
        @Size(max = 30) String numeroCaf,
        LocalDate validadeCaf,
        @NotBlank(message = "O municipio e obrigatorio") @Size(max = 80) String municipio,
        @NotBlank(message = "A UF e obrigatoria") @Pattern(regexp = "[A-Z]{2}", message = "A UF deve ter 2 letras maiusculas") String uf,
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 digitos") String telefone,
        @Email(message = "E-mail invalido") String email
) {
}
