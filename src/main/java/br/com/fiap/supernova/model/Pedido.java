package br.com.fiap.supernova.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_sn_pedido")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_comprador")
    private Comprador comprador;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusPedido status = StatusPedido.PENDENTE;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        itens.add(item);
        valorTotal = valorTotal.add(item.getSubtotal());
    }

    // fluxo: PENDENTE -> CONFIRMADO -> ENTREGUE, ou cancelar antes de entregar
    public boolean podeMudarPara(StatusPedido novo) {
        return switch (status) {
            case PENDENTE -> novo == StatusPedido.CONFIRMADO || novo == StatusPedido.CANCELADO;
            case CONFIRMADO -> novo == StatusPedido.ENTREGUE || novo == StatusPedido.CANCELADO;
            default -> false;
        };
    }
}
