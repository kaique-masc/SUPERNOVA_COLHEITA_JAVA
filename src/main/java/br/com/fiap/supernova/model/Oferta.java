package br.com.fiap.supernova.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_sn_oferta")
@Getter
@Setter
@NoArgsConstructor
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_produtor")
    private Produtor produtor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_produto")
    private Produto produto;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "qtd_disponivel", nullable = false, precision = 10, scale = 2)
    private BigDecimal qtdDisponivel;

    @Column(name = "data_colheita", nullable = false)
    private LocalDate dataColheita;

    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusOferta status = StatusOferta.DISPONIVEL;

    public boolean isVencida(LocalDate hoje) {
        return dataValidade.isBefore(hoje);
    }

    public void baixarEstoque(BigDecimal quantidade) {
        qtdDisponivel = qtdDisponivel.subtract(quantidade);
        if (qtdDisponivel.signum() == 0) {
            status = StatusOferta.ESGOTADA;
        }
    }

    public void devolverEstoque(BigDecimal quantidade) {
        qtdDisponivel = qtdDisponivel.add(quantidade);
        if (status == StatusOferta.ESGOTADA) {
            status = StatusOferta.DISPONIVEL;
        }
    }
}
