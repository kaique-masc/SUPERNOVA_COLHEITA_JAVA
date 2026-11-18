package br.com.fiap.supernova.dto;

import br.com.fiap.supernova.model.Comprador;
import br.com.fiap.supernova.model.TipoComprador;

import java.math.BigDecimal;

public record CompradorResponse(Long id, String nome, TipoComprador tipo, String cnpj, String municipio,
                                String uf, BigDecimal verbaPnaeAnual, String email) {

    public static CompradorResponse from(Comprador c) {
        return new CompradorResponse(c.getId(), c.getNome(), c.getTipo(), c.getCnpj(), c.getMunicipio(),
                c.getUf(), c.getVerbaPnaeAnual(), c.getEmail());
    }
}
