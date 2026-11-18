package br.com.fiap.supernova.dto;

import br.com.fiap.supernova.model.CategoriaProduto;
import br.com.fiap.supernova.model.UnidadeMedida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProdutoRequest(
        @NotBlank(message = "O nome e obrigatorio") @Size(max = 80) String nome,
        @NotNull(message = "A categoria e obrigatoria") CategoriaProduto categoria,
        @NotNull(message = "A unidade e obrigatoria") UnidadeMedida unidade
) {
}
