package br.com.fiap.supernova.dto;

import java.math.BigDecimal;

public record MetaPnaeResponse(Long compradorId, String escola, String municipio, int ano,
                               BigDecimal verbaPnaeAnual, BigDecimal valorComprado,
                               BigDecimal percentual, BigDecimal metaPercentual,
                               BigDecimal faltaParaMeta, String situacao) {
}
