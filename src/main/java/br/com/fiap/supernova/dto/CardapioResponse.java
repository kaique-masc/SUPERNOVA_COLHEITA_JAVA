package br.com.fiap.supernova.dto;

import br.com.fiap.supernova.model.StatusCardapio;
import br.com.fiap.supernova.model.UnidadeMedida;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CardapioResponse(Long id, LocalDateTime dataCriacao, Integer numeroAlunos, Integer diasLetivos,
                               BigDecimal orcamento, BigDecimal custoEstimado, boolean dentroDoOrcamento,
                               StatusCardapio status, Long pedidoId, List<Dia> dias, String observacoes,
                               List<Item> itens) {

    public record Dia(String dia, String prato, List<String> ingredientes) {
    }

    public record Item(Long ofertaId, String produtoNome, UnidadeMedida unidade, String produtorNome,
                       BigDecimal quantidade, BigDecimal precoUnitario, BigDecimal subtotal, String justificativa) {
    }
}
