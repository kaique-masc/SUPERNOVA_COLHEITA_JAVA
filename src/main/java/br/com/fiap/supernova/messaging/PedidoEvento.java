package br.com.fiap.supernova.messaging;

import br.com.fiap.supernova.model.ItemPedido;
import br.com.fiap.supernova.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoEvento(
        TipoEvento tipo,
        Long pedidoId,
        String status,
        Long compradorId,
        String compradorNome,
        List<Long> produtorIds,
        BigDecimal valorTotal,
        LocalDateTime dataHora
) {
    public enum TipoEvento { PEDIDO_CRIADO, STATUS_ALTERADO }

    public static PedidoEvento de(TipoEvento tipo, Pedido pedido) {
        List<Long> produtores = pedido.getItens().stream()
                .map(ItemPedido::getOferta)
                .map(o -> o.getProdutor().getId())
                .distinct()
                .toList();
        return new PedidoEvento(tipo, pedido.getId(), pedido.getStatus().name(),
                pedido.getComprador().getId(), pedido.getComprador().getNome(), produtores,
                pedido.getValorTotal(), LocalDateTime.now());
    }

    public String routingKey() {
        return "pedido." + tipo.name().toLowerCase();
    }
}
