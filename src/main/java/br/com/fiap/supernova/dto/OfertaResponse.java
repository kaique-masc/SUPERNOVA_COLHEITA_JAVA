package br.com.fiap.supernova.dto;

import br.com.fiap.supernova.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfertaResponse(Long id, Long produtorId, String produtorNome, String municipio, String uf,
                             Long produtoId, String produtoNome, CategoriaProduto categoria, UnidadeMedida unidade,
                             BigDecimal precoUnitario, BigDecimal qtdDisponivel, LocalDate dataColheita,
                             LocalDate dataValidade, StatusOferta status) {

    public static OfertaResponse from(Oferta o) {
        Produtor pr = o.getProdutor();
        Produto p = o.getProduto();
        return new OfertaResponse(o.getId(), pr.getId(), pr.getNome(), pr.getMunicipio(), pr.getUf(),
                p.getId(), p.getNome(), p.getCategoria(), p.getUnidade(), o.getPrecoUnitario(),
                o.getQtdDisponivel(), o.getDataColheita(), o.getDataValidade(), o.getStatus());
    }
}
