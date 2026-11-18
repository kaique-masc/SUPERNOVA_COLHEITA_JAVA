package br.com.fiap.supernova.dto;

import br.com.fiap.supernova.model.ItemPedido;
import br.com.fiap.supernova.model.Pedido;
import br.com.fiap.supernova.model.StatusPedido;
import br.com.fiap.supernova.model.UnidadeMedida;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(Long id, Long compradorId, String compradorNome, LocalDateTime dataPedido,
                             StatusPedido status, BigDecimal valorTotal, List<Item> itens) {

    public record Item(Long id, Long ofertaId, String produtoNome, UnidadeMedida unidade, Long produtorId,
                       String produtorNome, BigDecimal quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {

        static Item from(ItemPedido i) {
            var o = i.getOferta();
            return new Item(i.getId(), o.getId(), o.getProduto().getNome(), o.getProduto().getUnidade(),
                    o.getProdutor().getId(), o.getProdutor().getNome(), i.getQuantidade(),
                    i.getPrecoUnitario(), i.getSubtotal());
        }
    }

    public static PedidoResponse from(Pedido p) {
        return new PedidoResponse(p.getId(), p.getComprador().getId(), p.getComprador().getNome(),
                p.getDataPedido(), p.getStatus(), p.getValorTotal(),
                p.getItens().stream().map(Item::from).toList());
    }
}
