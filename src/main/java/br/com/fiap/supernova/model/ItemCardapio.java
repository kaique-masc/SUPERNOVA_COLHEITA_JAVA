package br.com.fiap.supernova.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_sn_item_cardapio")
@Getter
@Setter
@NoArgsConstructor
public class ItemCardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cardapio")
    private Cardapio cardapio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_oferta")
    private Oferta oferta;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    @Column(length = 300)
    private String justificativa;

    public ItemCardapio(Oferta oferta, BigDecimal quantidade, String justificativa) {
        this.oferta = oferta;
        this.quantidade = quantidade;
        this.justificativa = justificativa;
    }
}
