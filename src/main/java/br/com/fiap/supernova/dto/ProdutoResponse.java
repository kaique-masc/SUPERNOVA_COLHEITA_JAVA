package br.com.fiap.supernova.dto;

import br.com.fiap.supernova.model.CategoriaProduto;
import br.com.fiap.supernova.model.Produto;
import br.com.fiap.supernova.model.UnidadeMedida;

public record ProdutoResponse(Long id, String nome, CategoriaProduto categoria, UnidadeMedida unidade) {

    public static ProdutoResponse from(Produto p) {
        return new ProdutoResponse(p.getId(), p.getNome(), p.getCategoria(), p.getUnidade());
    }
}
