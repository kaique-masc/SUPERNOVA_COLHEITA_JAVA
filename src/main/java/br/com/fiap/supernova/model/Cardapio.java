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
@Table(name = "tb_sn_cardapio")
@Getter
@Setter
@NoArgsConstructor
public class Cardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_comprador")
    private Comprador comprador;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "numero_alunos", nullable = false)
    private Integer numeroAlunos;

    @Column(name = "dias_letivos", nullable = false)
    private Integer diasLetivos;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal orcamento;

    @Column(length = 500)
    private String restricoes;

    @Column(name = "custo_estimado", nullable = false, precision = 12, scale = 2)
    private BigDecimal custoEstimado = BigDecimal.ZERO;

    // dias do cardapio e observacoes gerados pela IA, em JSON
    @Lob
    @Column(nullable = false)
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusCardapio status = StatusCardapio.SUGERIDO;

    @OneToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @OneToMany(mappedBy = "cardapio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCardapio> itens = new ArrayList<>();

    public void adicionarItem(ItemCardapio item) {
        item.setCardapio(this);
        itens.add(item);
    }
}
