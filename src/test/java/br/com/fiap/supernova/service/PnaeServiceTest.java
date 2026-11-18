package br.com.fiap.supernova.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PnaeServiceTest {

    @Test
    void classificaSituacaoDaMeta() {
        assertThat(PnaeService.situacao(new BigDecimal("30.00"))).isEqualTo("META ATINGIDA");
        assertThat(PnaeService.situacao(new BigDecimal("15.86"))).isEqualTo("EM ANDAMENTO");
        assertThat(PnaeService.situacao(new BigDecimal("4.95"))).isEqualTo("CRITICO");
    }
}
