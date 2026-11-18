package br.com.fiap.supernova.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "tb_sn_item_pedido")
@Getter
@Setter
@NoArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_oferta")
    private Oferta oferta;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    // copiado da oferta na hora da compra
    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public ItemPedido(Oferta oferta, BigDecimal quantidade) {
        this.oferta = oferta;
        this.quantidade = quantidade;
        this.precoUnitario = oferta.getPrecoUnitario();
        this.subtotal = quantidade.multiply(precoUnitario).setScale(2, RoundingMode.HALF_UP);
    }
}
