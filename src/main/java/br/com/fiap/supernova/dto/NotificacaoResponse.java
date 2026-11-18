package br.com.fiap.supernova.dto;

import br.com.fiap.supernova.model.Notificacao;

import java.time.LocalDateTime;

public record NotificacaoResponse(Long id, String titulo, String mensagem, Long pedidoId, boolean lida,
                                  LocalDateTime dataCriacao) {

    public static NotificacaoResponse from(Notificacao n) {
        return new NotificacaoResponse(n.getId(), n.getTitulo(), n.getMensagem(), n.getPedidoId(),
                n.isLida(), n.getDataCriacao());
    }
}
